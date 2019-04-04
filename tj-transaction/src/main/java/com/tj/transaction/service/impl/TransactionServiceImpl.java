package com.tj.transaction.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tj.dto.*;
import com.tj.transaction.bridge.TransactionStatus;
import com.tj.transaction.dao.*;
import com.tj.transaction.domain.UserBalanceInfo;
import com.tj.transaction.domain.UserTransactionRecd;
import com.tj.transaction.domain.UserTransactionRecdExample;
import com.tj.transaction.service.CommonService;
import com.tj.transaction.service.TransactionService;
import com.tj.transaction.strategy.AbstractTransactionStrategy;
import com.tj.util.A.SacException;
import com.tj.util.Results;
import com.tj.util.SacRecdStatusEnum;
import com.tj.util.TransactionTypeEnum;
import com.tj.util.aspect.CommonLogAspect;
import com.tj.util.enums.AuditStatusEnum;
import com.tj.util.enums.DateTypeEnum;
import com.tj.util.enums.PlatformTypeEnum;
import com.tj.util.enums.PollingStatusEnum;
import com.tj.util.log.Rlog;
import com.tj.util.redis.CloudRedisService;
import com.tj.util.unique.Unique;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: tj-core
 * @description: 交易业务
 * @author: liang.song
 * @create: 2018-11-27-18:29
 **/
@Service
public class TransactionServiceImpl implements TransactionService {

    private final CommonService commonService;
    private final CloudRedisService cloudRedisService;
    private final Rlog rlog;
    @Resource
    private UserBalanceInfoMapper userBalanceInfoMapper;
    @Resource
    private UserTransactionRecdMapperEx userTransactionRecdMapperEx;
    @Resource
    private UserTransactionRecdMapper userTransactionRecdMapper;
    private Map<PlatformTypeEnum, AbstractTransactionStrategy> strategyManager = new ConcurrentHashMap<>();
    @Value("${transaction.max-withdraw-amount}")
    private BigDecimal maxWithdrawAmount;
    @Value("${transaction.fee-ratio}")
    private BigDecimal feeRatio;
    @Autowired
    private CommonLogAspect commonLogAspect;
    @Autowired
    private Unique unique;

    @Value("${transaction.min-amount}")
    private BigDecimal minAmount;
    @Value("${transaction.max-amount}")
    private BigDecimal maxAmount;
    @Resource
    private UserInfoMapper userInfoMapper;
    private final List<TransactionStatus> transactionStatusList;
    private Map<String, TransactionStatus> transactionStatusMap = new ConcurrentHashMap<>();
    @Resource
    private UserBalanceInfoMapperEx userBalanceInfoMapperEx;
    @Autowired
    public TransactionServiceImpl(Rlog rlog, CommonService commonService, List<AbstractTransactionStrategy> strategies, CloudRedisService cloudRedisService, List<TransactionStatus> transactionStatusList) {
        this.commonService = commonService;
        this.rlog = rlog;
        for (AbstractTransactionStrategy strategy : strategies) {
            strategyManager.put(strategy.type(), strategy);
        }
        this.cloudRedisService = cloudRedisService;
        this.transactionStatusList = transactionStatusList;

        transactionStatusList.forEach(v -> {
            transactionStatusMap.put(v.code(), v);
        });
    }

    /**
     * 提交充值 TODO 提交充值的流程需要和SDK 接入
     * <p>
     * 1.提交充值请求-新建一条充值记录,交易状态为交易中 事务性操作
     * 2.发起远程交易请求,解析交易结果
     * </p>
     *
     * @param requestDto 充值请求
     * @return 交易详情
     */
    @Override
    public Results.Result<TransactionResultDto> charge(TransactionRequestDto requestDto) {
        //插入充值记录
        Long transactionId = commonService.insertTransaction(requestDto);
        //发起消息
        RemoteChargeResult remoteChargeResult = strategyManager.get(PlatformTypeEnum.codeOf(requestDto.getPlatform())).charge(transactionId);
        //更新充值第三方订单ID
        commonService.updateTransaction(remoteChargeResult);
        TransactionResultDto result = commonService.getTransactionResult(transactionId, remoteChargeResult.getData().getScalper_url());
        return new Results.Result<>(Results.SUCCESS, result);
    }

    /**
     * 交易回调
     * <p>
     * 1.判断此订单是否存在
     * 不存在->提示错误信息
     * 存在->2
     * 2.校验金额是否正确
     * 不正确->提示错误
     * 正确->3
     * 3.校验订单状态
     * 已经落地:(成功/失败/处理中) 则订单不再进行处理
     * 还是处理中->4
     * 4.更新订单状态
     * 交易失败->更新记录为失败
     * 交易成功->更新记录为成功->用户账户账户加钱->积分变动(商户积分->个人用户)
     * 5.整个方法为事务性操作
     * </p>
     *
     * @return 回调结果
     */
    @Override
    public CallbackReturnDto transactionCallback(CallbackDto callbackDto) {
        rlog.debug("callback param:{}", JSON.toJSONString(callbackDto));
        UserTransactionRecd userTransactionRecd = userTransactionRecdMapper.selectByPrimaryKey(Long.valueOf(callbackDto.getCompany_order_id()));
        if (userTransactionRecd == null) {
            rlog.error("transaction recd is not existed:{}", callbackDto.getCompany_order_id());
            return CallbackReturnDto.builder().code(Results.Result.TX_FAIL).msg("订单不存在").build();
        }
        //比较金额
        BigDecimal actualAmount = userTransactionRecd.getActualAmount();
        if (actualAmount.compareTo(callbackDto.getOriginal_amount()) != 0) {
            rlog.error("transaction amount not equal:dbAmount:{}->callbackAmount:{}", actualAmount, callbackDto.getOriginal_amount());
            return CallbackReturnDto.builder().code(Results.Result.TX_FAIL).msg("交易金额不相等").build();
        }
        //校验订单状态
        Integer recdStatus = userTransactionRecd.getRecdStatus();
        if (!recdStatus.equals(SacRecdStatusEnum.processing.getCode())) {
            rlog.warn("recd :{} status is :{} ,is not processing,no need to update again", callbackDto.getCompany_order_id(), recdStatus);
            return CallbackReturnDto.builder().code(Results.Result.SUCCESS).msg("交易状态已经落地").build();
        }
        //更新订单状态并加钱
        //所有的支付回调都是成功的
        transactionStatusMap.get(SacRecdStatusEnum.success.getCode().toString()).handler(userTransactionRecd.getId());
        return CallbackReturnDto.builder().code(Results.Result.SUCCESS).msg("交易成功").build();
    }

    public void polling(String transactionId, Integer status) {
        switch (PollingStatusEnum.codeOf(status)) {
            case OK:
                transactionStatusMap.get(SacRecdStatusEnum.success.getCode().toString()).handler(Long.valueOf(transactionId));
                break;
            case FAIL:
                transactionStatusMap.get(SacRecdStatusEnum.fail.getCode().toString()).handler(Long.valueOf(transactionId));
                break;
            case PROCESS:
                break;
            case NOT_EXISTED:
                transactionStatusMap.get(SacRecdStatusEnum.fail.getCode().toString()).handler(Long.valueOf(transactionId));
                break;
        }
    }
    /**
     * 提现
     * 1.冻结提现金额
     * 2.插入一条提现记录-状态为提现中
     * 3.发起远程交易
     * 3.1 解析提现结果，保留第三方订单号,可能会直接返回提现结果
     * 4.根据提现结果解冻金额
     * 4.1 提现成功->更新提现记录状态为成功->扣除冻结金额->插入积分变动记录
     * 4.2 提现失败->更新提现记录状态为失败->回滚提现金额
     * 5.在提现中发生异常->保持提现记录状态为提现中
     * 6. step 4.1&4.2是一个事务   2.为一个事务
     *
     * @param requestDto 交易请求
     * @return 提现结果
     */
    @Override
    public Results.Result<TransactionResultDto> withdraw(TransactionRequestDto requestDto) {

        Long transactionId = commonService.insertWithdrawAndFreezeAmount(requestDto);
        //加入判断
        //如果是提现 且提现金额超过指定数额 要进行审核
        if (requestDto.getType().equals(TransactionTypeEnum.withdraw.getCode())) {
            if (new BigDecimal(requestDto.getAmount()).compareTo(maxWithdrawAmount) >= 0) {
                //提现金额超过最大值，需要审核
                return new Results.Result<>(Results.SUCCESS, commonService.getTransactionResult(transactionId, null));
            }
        }
        RemoteWithdrawResult remoteWithdrawResult = strategyManager.get(PlatformTypeEnum.codeOf(requestDto.getPlatform())).withdraw(transactionId);

        commonService.updateWithdrawStatus(remoteWithdrawResult);

        TransactionResultDto result = commonService.getTransactionResult(transactionId, null);

        return new Results.Result<>(Results.SUCCESS, result);
    }

    @Override
    public Results.Result<UserBalanceInfoDto> userBalance() {
        RedisUserInfo redisUser = commonLogAspect.currentUser();
        Integer userId = redisUser.getUserId();
        UserBalanceInfo userBalanceInfo = userBalanceInfoMapper.selectByPrimaryKey(userId);
        BigDecimal amount = userBalanceInfo.getAmount();
        BigDecimal freazonAmount = userBalanceInfo.getFreazonAmount();
        UserBalanceInfoDto build = UserBalanceInfoDto.builder().amount(amount).freazonAmount(freazonAmount).userId(userId).build();
        return new Results.Result<>(Results.SUCCESS, build);
    }

    @Override
    public Results.Result<UserTransactionSumDto> list(Integer type, Integer dateType, Integer platform, Integer recdStatus, Integer curPage, Integer pageSize) {
        //交易记录列表
        RedisUserInfo redisUserInfo = commonLogAspect.currentUser();
        Integer userId = redisUserInfo.getUserId();
        Date startTime = Date.from(LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)));
        final Date endTime = startTime;//默认结束时间是当前时间
        switch (DateTypeEnum.codeOf(dateType)) {
            case Week:
                startTime = Date.from(LocalDate.now().minusDays(7).atStartOfDay(ZoneOffset.ofHours(8)).toInstant());
                break;
            case month:
                startTime = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneOffset.ofHours(8)).toInstant());
                break;

            case year:
                startTime = Date.from(LocalDate.now().withDayOfYear(1).atStartOfDay(ZoneOffset.ofHours(8)).toInstant());
                break;
            case all:
                startTime = null;
        }
        Date finalStartTime = startTime;
        PageInfo<UserTransactionRecdDto> pageInfo = PageHelper.startPage(curPage, pageSize).doSelectPageInfo(() -> {
            userTransactionRecdMapperEx.list(userId, type, platform ,recdStatus,finalStartTime,endTime);
        });
        UserTransactionSumDto sumDto = userTransactionRecdMapperEx.sumUserTransaction(userId, type,platform ,recdStatus, finalStartTime, endTime);
        if (sumDto != null) {
            sumDto.setPages(pageInfo);
        }
        return new Results.Result<>(Results.SUCCESS, sumDto);
    }

    @Override
    public Results.Result<AdminTransactionSumDto> adminRecdList(Integer type, Integer recdStatus, Integer auditStatus, String username,
                                                                String phone, String transactionId, Date startTime, Date endTime,
                                                                Integer curPage, Integer pageSize) {
        PageInfo<AdminTransactionRecdDto> adminPageInfo = PageHelper.startPage(curPage, pageSize).doSelectPageInfo(() -> {
            userTransactionRecdMapperEx.listTransaction(type, recdStatus, auditStatus, username, phone, transactionId, startTime, endTime);
        });
        AdminTransactionSumDto sumDto = userTransactionRecdMapperEx.sumTransaction(type, recdStatus, auditStatus, username, phone, transactionId, startTime, endTime);
        if (sumDto != null) {
            sumDto.setPages(adminPageInfo);
        }
        return new Results.Result<>(Results.SUCCESS, sumDto);
    }

    public Results.Result<Void> auditWithdraw(Long transactionId, Integer auditStatus, String auditRemark) {
        RedisUserInfo admin = commonLogAspect.currentUser();
        UserTransactionRecd recd = userTransactionRecdMapper.selectByPrimaryKey(transactionId);
        if (recd == null) {
            return new Results.Result<>(Results.Result.SYSTEM_BUSY, "订单不存在", null);
        }
        UserTransactionRecd transactionRecd = new UserTransactionRecd();
        transactionRecd.setId(recd.getId());
        transactionRecd.setAuditStatus(auditStatus);
        transactionRecd.setAuditRemark(auditRemark);
        transactionRecd.setAuditTime(new Date());
        transactionRecd.setAuditUser(admin.getUserId());
        userTransactionRecdMapper.updateByPrimaryKeySelective(transactionRecd);
        if (auditStatus.equals(AuditStatusEnum.SUCCESS.getCode())) {
            //审核成功,发起提现请求
            RemoteWithdrawResult remoteWithdrawResult = strategyManager.get(PlatformTypeEnum.codeOf(recd.getPlatform())).withdraw(transactionId);
            commonService.updateWithdrawStatus(remoteWithdrawResult);
        }
        //TODO 增加提现审核失败-回滚用户金额的逻辑
        return new Results.Result<>(Results.SUCCESS, null);
    }

    /**
     * 创建线下订单
     * <p>
     * 1.创建订单(recd_type:充值，recd_status:处理中 ，audit_status:审核中，platform:线下充值，如果有用户备注，请填写用户备注)
     * </p>
     *
     * @param requestDto 线下订单
     * @return
     */
    @Override
    @Transactional
    public Results.Result<Void> createOfflineTransaction(OfflineTransactionRequestDto requestDto) {
        if (requestDto.getAmount() == null || new BigDecimal(requestDto.getAmount()).compareTo(BigDecimal.ZERO) <= 0) {
            return Results.PARAMETER_INCORRENT;
        }
        if (requestDto.getPhone() == null) {
            return Results.PARAMETER_INCORRENT;
        }
        BigDecimal actualAmount = new BigDecimal(requestDto.getAmount());
        if (actualAmount.compareTo(minAmount) < 0) {
            rlog.error("transaction amount {} less than {}", actualAmount, minAmount);
            return new Results.Result<>(Results.Result.SYSTEM_BUSY, "交易金额至少" + minAmount, null);
        }
        if (actualAmount.compareTo(maxAmount) > 0) {
            rlog.error("transaction amount {} great than {}", actualAmount, maxAmount);
            return new Results.Result<>(Results.Result.SYSTEM_BUSY, "交易金额最多" + maxAmount, null);
        }

        Long id = unique.nextId();
        UserTransactionRecd recd = new UserTransactionRecd();
        recd.setId(id);
        recd.setThirdPartyId(requestDto.getThirdPartyId());
        recd.setRecdType(TransactionTypeEnum.charge.getCode());
        recd.setAmount(new BigDecimal(requestDto.getAmount()));
        recd.setActualAmount(new BigDecimal(requestDto.getAmount()));
        recd.setUserId(commonLogAspect.currentUser().getUserId());
        recd.setVersion(0);
        recd.setRecdStatus(SacRecdStatusEnum.processing.getCode());
        recd.setCreateTime(new Date());
        recd.setUserRemark(requestDto.getUserRemark());
        recd.setSysRemark(null);
        recd.setAuditStatus(AuditStatusEnum.AUDITING.getCode());
        recd.setPlatform(PlatformTypeEnum.codeOf(requestDto.getPlatformType()).getCode());
        recd.setSnapshot(requestDto.getSnapshot());
        int insert = userTransactionRecdMapper.insertSelective(recd);
        if (insert != 1) {
            throw new SacException("创建线下订单失败,订单已经存在");
        }
        return Results.SUCCESS;
    }

    @Transactional
    public Results.Result<Void> auditOfflineTransaction(AdminAuditOfflineTransaction request) {
        UserTransactionRecd recd = userTransactionRecdMapper.selectByPrimaryKey(Long.valueOf(request.getTransactionId()));
        if (recd == null) {
            throw new SacException("充值不存在,交易不存在");
        }
        Integer platform = recd.getPlatform();
        if (platform.equals(PlatformTypeEnum.alipay.getCode()) || platform.equals(PlatformTypeEnum.wechat.getCode()) || platform.equals(PlatformTypeEnum.credit.getCode())) {
            throw new SacException("审核订单类型错误,非线下订单");
        }
        Integer recdStatus = recd.getRecdStatus();
        if (recdStatus.equals(SacRecdStatusEnum.success.getCode())) {
            throw new SacException("此订单已经充值成功,无需再次充值");
        }
        Integer auditStatus = recd.getAuditStatus();
        if (auditStatus.equals(AuditStatusEnum.SUCCESS.getCode())) {
            throw new SacException("此订单已经充值成功,无需再次充值");
        }
        //标记此订单为审核成功-订单交易状态为成功
        RedisUserInfo admin = commonLogAspect.currentUser();
        UserTransactionRecd transactionRecd = new UserTransactionRecd();
        transactionRecd.setAuditRemark(request.getAuditRemark());
        transactionRecd.setId(Long.valueOf(request.getTransactionId()));
        transactionRecd.setAuditUser(admin.getUserId());
        transactionRecd.setAuditTime(new Date());
        transactionRecd.setSnapshot(request.getSnapshot());
        switch (AuditStatusEnum.codeOf(request.getAuditStatus())) {
            case SUCCESS:
                transactionRecd.setAuditStatus(AuditStatusEnum.SUCCESS.getCode());
                userTransactionRecdMapper.updateByPrimaryKeySelective(transactionRecd);
                transactionStatusMap.get(SacRecdStatusEnum.success.getCode().toString()).handler(recd.getId());
                break;
            case FAIL:
                transactionRecd.setAuditStatus(AuditStatusEnum.FAIL.getCode());
                userTransactionRecdMapper.updateByPrimaryKeySelective(transactionRecd);
                transactionStatusMap.get(SacRecdStatusEnum.fail.getCode().toString()).handler(recd.getId());
                break;
            default:
                throw new SacException("不支持的审核状态");
        }
        return Results.SUCCESS;
    }

    /**
     * 用户提交提现申请
     * <p>
     * 1.用户提交提现申请
     * 2.提现状态为 提现中
     * 3.审核状态为审核中
     * 4.冻结用户提现金额
     * 5.整个流程是一个事务
     *
     * </p>
     *
     * @param request 申请内容
     * @return 提交成功
     */
    @Override
    @Transactional
    public Results.Result<Void> submitWithdraw(UserWithdrawRequest request) {
        //校验提现金额是否正确
        BigDecimal withdrawAmount = new BigDecimal(request.getAmount());
        if (withdrawAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return new Results.Result<>(Results.Result.PARAMETER_INCORRENT, "提现金额不正确");
        }
        if (withdrawAmount.scale() > 2) {
            return new Results.Result<>(Results.Result.PARAMETER_INCORRENT, "提现金额不正确");
        }
        if (withdrawAmount.compareTo(minAmount) < 0) {
            rlog.error("transaction amount {} less than {}", withdrawAmount, minAmount);
            return new Results.Result<>(Results.Result.SYSTEM_BUSY, "交易金额至少" + minAmount, null);
        }
        if (withdrawAmount.compareTo(maxAmount) > 0) {
            rlog.error("transaction amount {} great than {}", withdrawAmount, maxAmount);
            return new Results.Result<>(Results.Result.SYSTEM_BUSY, "交易金额最多" + maxAmount, null);
        }
        Long transactionId = unique.nextId();
        BigDecimal fee = new BigDecimal(request.getAmount()).multiply(feeRatio).setScale(2, BigDecimal.ROUND_HALF_UP);
        RedisUserInfo user = commonLogAspect.currentUser();
        UserTransactionRecd recd = new UserTransactionRecd();
        recd.setAuditTime(null);
        recd.setAuditUser(null);
        recd.setAuditStatus(AuditStatusEnum.AUDITING.getCode());
        recd.setId(transactionId);
        recd.setAuditRemark(null);
        recd.setPlatform(PlatformTypeEnum.codeOf(request.getWithdrawType()).getCode());
        recd.setRecdStatus(SacRecdStatusEnum.processing.getCode());
        recd.setSysRemark(null);
        recd.setUserRemark(request.getUserRemark());
        recd.setCreateTime(new Date());
        recd.setVersion(0);
        recd.setUserId(user.getUserId());
        recd.setActualAmount(withdrawAmount.subtract(fee).setScale(2, BigDecimal.ROUND_HALF_UP));
        recd.setAmount(new BigDecimal(request.getAmount()));
        recd.setRecdType(TransactionTypeEnum.withdraw.getCode());
        recd.setThirdPartyId(null);//TODO 管理员在打完钱之后，需要回填回执单号,以及保留交易快照
        recd.setFee(fee);
        recd.setFeeRatio(feeRatio);
        recd.setTargetAddr(request.getWithdrawAccount());
        int insert = userTransactionRecdMapper.insertSelective(recd);
        if (insert != 1) {
            throw new SacException("提现失败");
        }
        //冻结用户金额
        commonService.freezeWithdrawAmount(user.getUserId(), withdrawAmount);
        return new Results.Result<>(Results.SUCCESS, null);
    }

    /**
     * 管理员确认提现结果
     * <p>
     * 1.查询订单是否存在
     * 2.校验订单是否已经审核过
     * 3.确认审核结果
     * 审核失败->提现冻结金额回滚
     * 审核成功->提现金额扣除->插入商户&用户积分变动
     * 4.更新提现记录的审核状态-审核人-审核备注-第三方订单号-交易状态-交易快照
     * <p>
     * 5.整个是一个事务
     * </p>
     *
     * @param request 确认提现
     * @return
     */
    @Override
    @Transactional
    public Results.Result<Void> adminConfirmWithdraw(AuditWithdrawRequest request) {
        RedisUserInfo admin = commonLogAspect.currentUser();
        String withdrawId = request.getWithdrawId();
        UserTransactionRecd transactionRecd = userTransactionRecdMapper.selectByPrimaryKey(Long.valueOf(withdrawId));
        if (transactionRecd == null) {
            return new Results.Result<>(Results.Result.NOT_FIND, "提现记录不存在");
        }
        Integer auditStatus = transactionRecd.getAuditStatus();//数据库的状态
        if (!auditStatus.equals(AuditStatusEnum.AUDITING.getCode())) {
            return new Results.Result<>(Results.Result.SYSTEM_BUSY, "订单已经审核");
        }
        UserTransactionRecd updateRecd = new UserTransactionRecd();
        updateRecd.setAuditUser(admin.getUserId());
        updateRecd.setAuditStatus(request.getAuditStatus());
        updateRecd.setAuditRemark(request.getAuditRemark());
        updateRecd.setAuditTime(new Date());
        updateRecd.setThirdPartyId(request.getThirdPartyId());
        updateRecd.setSnapshot(request.getAuditSnapshot());
        UserTransactionRecdExample example = new UserTransactionRecdExample();
        example.or().andIdEqualTo(transactionRecd.getId()).andRecdStatusEqualTo(transactionRecd.getRecdStatus()).andAuditStatusEqualTo(transactionRecd.getAuditStatus());
        int update = userTransactionRecdMapper.updateByExampleSelective(updateRecd, example);
        if (update != 1) {
            throw new SacException("确认提现审核失败");
        }
        switch (AuditStatusEnum.codeOf(request.getAuditStatus())) {
            case SUCCESS:
                transactionStatusMap.get(SacRecdStatusEnum.success.getCode().toString()).handler(transactionRecd.getId());
                break;
            case FAIL:
                transactionStatusMap.get(SacRecdStatusEnum.fail.getCode().toString()).handler(transactionRecd.getId());
                break;
        }
        return new Results.Result<>(Results.SUCCESS, null);
    }


}
