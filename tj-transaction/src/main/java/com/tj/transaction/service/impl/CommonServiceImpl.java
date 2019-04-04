package com.tj.transaction.service.impl;

import com.tj.dto.*;
import com.tj.transaction.dao.UserBalanceChangeRecdMapper;
import com.tj.transaction.dao.UserBalanceInfoMapperEx;
import com.tj.transaction.dao.UserInfoMapper;
import com.tj.transaction.dao.UserTransactionRecdMapper;
import com.tj.transaction.domain.UserBalanceChangeRecd;
import com.tj.transaction.domain.UserTransactionRecd;
import com.tj.transaction.service.CommonService;
import com.tj.util.A.SacException;
import com.tj.util.SacRecdStatusEnum;
import com.tj.util.TransactionTypeEnum;
import com.tj.util.aspect.CommonLogAspect;
import com.tj.util.enums.AuditStatusEnum;
import com.tj.util.enums.OptTypeEnum;
import com.tj.util.enums.otc.SubOrAddEnum;
import com.tj.util.log.Rlog;
import com.tj.util.redis.CloudRedisService;
import com.tj.util.unique.Unique;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: tj-core
 * @description: 公用服务
 * @author: liang.song
 * @create: 2018-11-27-19:30
 **/
@Service
public class CommonServiceImpl implements CommonService {
    private final CloudRedisService cloudRedisService;
    private final Unique unique;
    private final Rlog rlog;
    @Resource
    private UserTransactionRecdMapper userTransactionRecdMapper;
    @Resource
    private UserBalanceInfoMapperEx userBalanceInfoMapperEx;
    @Resource
    private UserBalanceChangeRecdMapper userBalanceChangeRecdMapper;
    @Resource
    private UserInfoMapper userInfoMapper;

    @Value("${transaction.max-withdraw-amount}")
    private BigDecimal maxWithdrawAmount;
    @Value("${transaction.fee-ratio}")
    private BigDecimal feeRatio;
    @Autowired
    private CommonLogAspect commonLogAspect;
    @Autowired
    public CommonServiceImpl(CloudRedisService cloudRedisService, Unique unique, Rlog rlog) {
        this.cloudRedisService = cloudRedisService;
        this.unique = unique;
        this.rlog = rlog;

    }

    @Transactional
    @Override
    public Long insertTransaction(TransactionRequestDto requestDto) {
        rlog.info("insert transaction recd ..........");
        RedisUserInfo redisUser = commonLogAspect.currentUser();
        Integer userId = redisUser.getUserId();
        Long transactionId = unique.nextId();
        UserTransactionRecd recd = new UserTransactionRecd();
        recd.setId(transactionId);
        recd.setUserId(userId);
        recd.setAmount(new BigDecimal(requestDto.getAmount()));//交易金额
        recd.setActualAmount(new BigDecimal(requestDto.getAmount()));//默认等于当前金额
        recd.setAuditRemark(null);
        recd.setAuditStatus(AuditStatusEnum.SUCCESS.getCode());//默认是审核成功
        recd.setFee(null);//交易手续费
        recd.setFeeRatio(BigDecimal.ZERO);//交易税率
        //如果是提现 且提现金额超过指定数额 要进行审核
        if (requestDto.getType().equals(TransactionTypeEnum.withdraw.getCode())) {
            if (new BigDecimal(requestDto.getAmount()).compareTo(maxWithdrawAmount) >= 0) {
                //提现金额超过最大值，需要审核
                recd.setAuditStatus(AuditStatusEnum.AUDITING.getCode());
            }
            //提现扣除手续费
            String amount = requestDto.getAmount();//交易金额
            BigDecimal fee = new BigDecimal(amount).multiply(feeRatio).setScale(2, BigDecimal.ROUND_HALF_UP);//TODO 确定交易费率的精确规则
            BigDecimal actualAmount = new BigDecimal(amount).subtract(fee);//实际交易金额
            recd.setFeeRatio(feeRatio);
            recd.setFee(fee);
            recd.setActualAmount(actualAmount);
        }
        recd.setAuditTime(null);
        recd.setAuditUser(null);
        recd.setCreateTime(new Date());
        recd.setPlatform(requestDto.getPlatform());
        recd.setRecdStatus(SacRecdStatusEnum.processing.getCode());
        recd.setRecdType(TransactionTypeEnum.codeOf(requestDto.getType()).getCode());
        recd.setSysRemark(null);
        recd.setTargetAddr(requestDto.getTargetAddr());
        recd.setThirdPartyId(null);
        recd.setUpdateTime(new Date());
        recd.setUserRemark(null);
        int insert = userTransactionRecdMapper.insertSelective(recd);
        if (insert != 1) {
            throw new RuntimeException("插入交易记录失败");
        }
        return transactionId;
    }

    @Transactional
    @Override
    public Long updateTransaction(RemoteChargeResult remoteChargeResult) {
        rlog.info("update transaction recd thirdPartyId ..........");
        RemoteChargeData data = remoteChargeResult.getData();
        String transactionId = data.getCompany_order_id();

        UserTransactionRecd recd = userTransactionRecdMapper.selectByPrimaryKey(Long.valueOf(transactionId));
        if (recd == null) {
            rlog.error("transaction recd is not existed:{}", transactionId);
            throw new SacException("交易记录不存在,交易失败");
        }
        UserTransactionRecd updateRecd = new UserTransactionRecd();
        updateRecd.setId(Long.valueOf(transactionId));
        updateRecd.setThirdPartyId(data.getDora_order_id());
        updateRecd.setSysRemark(remoteChargeResult.getMsg());
        int update = userTransactionRecdMapper.updateByPrimaryKeySelective(updateRecd);
        if (update != 1) {
            rlog.error("update transaction recd thirdPartyId got wrong:{}", transactionId);
            throw new SacException("交易失败,请稍后重试");
        }
        return Long.valueOf(transactionId);

    }

    @Override
    public TransactionResultDto getTransactionResult(Long transactionId, String payUrl) {
        UserTransactionRecd recd = userTransactionRecdMapper.selectByPrimaryKey(transactionId);
        if (recd == null) {
            rlog.error("transaction do not existed:{}", transactionId);
            throw new SacException("交易记录不存在,交易失败");
        }
        return TransactionResultDto.builder()
                .id(transactionId.toString())
                .amount(recd.getActualAmount())
                .createTime(new Date())
                .status(recd.getRecdStatus())
                .type(recd.getRecdType())
                .userId(recd.getUserId())
                .payUrl(payUrl)
                .build();
    }


    @Transactional
    @Override
    public Long insertWithdrawAndFreezeAmount(TransactionRequestDto requestDto) {
        RedisUserInfo redisUser = commonLogAspect.currentUser();
        Integer userId = redisUser.getUserId();
        //冻结用户金额
        freezeWithdrawAmount(userId, new BigDecimal(requestDto.getAmount()));
        //插入交易记录
        return insertTransaction(requestDto);
    }

    @Transactional
    @Override
    public void freezeWithdrawAmount(Integer userId, BigDecimal amount) {
        //冻结用户金额
        int update = userBalanceInfoMapperEx.freezeWithdrawAmount(userId, amount);
        if (update != 1) {
            rlog.error("freeze user amount get error");
            throw new SacException("用户余额不足,提现失败");
        }
    }

    @Transactional
    @Override
    public void updateWithdrawStatus(RemoteWithdrawResult remoteWithdrawResult) {

        rlog.info("update withdraw recd thirdPartyId ..........");
        RemoteWithdrawData data = remoteWithdrawResult.getData();
        String transactionId = data.getCompany_order_id();

        UserTransactionRecd recd = userTransactionRecdMapper.selectByPrimaryKey(Long.valueOf(transactionId));
        if (recd == null) {
            rlog.error("withdraw recd is not existed:{}", transactionId);
            throw new SacException("交易记录不存在,交易失败");
        }
        UserTransactionRecd withdrawRecd = new UserTransactionRecd();
        withdrawRecd.setId(Long.valueOf(transactionId));
        withdrawRecd.setThirdPartyId(data.getDora_order_id());
        withdrawRecd.setSysRemark(remoteWithdrawResult.getMsg());
        int update = userTransactionRecdMapper.updateByPrimaryKeySelective(withdrawRecd);
        if (update != 1) {
            rlog.error("update withdraw recd thirdPartyId got wrong:{}", transactionId);
            throw new SacException("交易失败,请稍后重试");
        }
    }

    @Transactional
    public void insertUserBalanceChange(Long transactionId, BigDecimal amount, SubOrAddEnum subOrAddEnum, Integer userId) {
        UserBalanceChangeRecd recd = new UserBalanceChangeRecd();
        Long changeId = unique.nextId();
        recd.setId(changeId);
        recd.setAmount(amount);
        recd.setCreateTime(new Date());
        recd.setOptType(OptTypeEnum.charge.getCode());
        recd.setRelatedRecdId(transactionId);
        recd.setSubOrAdd(subOrAddEnum.ordinal());
        recd.setSysRemark(null);
        recd.setUserId(userId);
        recd.setVersion(0);
        int insertMerchantRecord = userBalanceChangeRecdMapper.insertSelective(recd);
        if (insertMerchantRecord != 1) {
            rlog.error("insert merchant balance change recd got error");
            throw new SacException("交易失败");
        }
    }

}
