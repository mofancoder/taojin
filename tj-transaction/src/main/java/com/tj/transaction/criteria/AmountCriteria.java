package com.tj.transaction.criteria;

import com.tj.dto.RedisUserInfo;
import com.tj.dto.TransactionRequestDto;
import com.tj.transaction.dao.UserBalanceInfoMapper;
import com.tj.transaction.domain.UserBalanceInfo;
import com.tj.util.Results;
import com.tj.util.TransactionTypeEnum;
import com.tj.util.aspect.CommonLogAspect;
import com.tj.util.log.Rlog;
import com.tj.util.redis.CloudRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @program: tj-core
 * @description: 交易金额过滤
 * @author: liang.song
 * @create: 2018-11-27-17:47
 **/
@Component
public class AmountCriteria implements Criteria {
    private final Rlog rlog;
    @Resource
    private UserBalanceInfoMapper userBalanceInfoMapper;
    @Autowired
    private CloudRedisService cloudRedisService;
    @Autowired
    public AmountCriteria(Rlog rlog) {
        this.rlog = rlog;
    }
    @Override
    public String name() {
        return "交易金额过滤";
    }

    @Value("${transaction.min-amount}")
    private BigDecimal minAmount;
    @Value("${transaction.max-amount}")
    private BigDecimal maxAmount;
    @Autowired
    private CommonLogAspect commonLogAspect;
    @Override
    public Results.Result<Boolean> meetCriteria(TransactionRequestDto requestDto) {
        String amount = requestDto.getAmount();
        if (StringUtils.isEmpty(amount)) {
            rlog.error("transaction amount is empty");
            return new Results.Result<>(Results.Result.SYSTEM_BUSY, "交易金额不正确", false);
        }
        BigDecimal actualAmount = new BigDecimal(amount);
        if (actualAmount.compareTo(BigDecimal.ZERO) <= 0) {
            rlog.error("transaction amount is {} less than 0", actualAmount);
            return new Results.Result<>(Results.Result.SYSTEM_BUSY, "交易金额不正确", false);
        }
        int scale = actualAmount.scale();//精度
        if (scale > 2) {
            rlog.error("transaction amount scale is:{},wrong", scale);
            return new Results.Result<>(Results.Result.SYSTEM_BUSY, "交易金额不正确", false);
        }
        if (actualAmount.compareTo(minAmount) < 0) {
            rlog.error("transaction amount {} less than {}", actualAmount, minAmount);
            return new Results.Result<>(Results.Result.SYSTEM_BUSY, "交易金额至少" + minAmount, false);
        }
        if (actualAmount.compareTo(maxAmount) > 0) {
            rlog.error("transaction amount {} great than {}", actualAmount, maxAmount);
            return new Results.Result<>(Results.Result.SYSTEM_BUSY, "交易金额最多" + maxAmount, false);
        }
        if (requestDto.getType().equals(TransactionTypeEnum.withdraw.getCode())) {
            //提现校验用户余额是否充足
            RedisUserInfo redisUser = commonLogAspect.currentUser();

            if (redisUser == null) {
                rlog.error("user is expired");
                return new Results.Result<>(Results.ACCOUNT_INVALID, null);
            }
            UserBalanceInfo balanceInfo = userBalanceInfoMapper.selectByPrimaryKey(redisUser.getUserId());
            if (balanceInfo == null) {
                rlog.error("user:{} do not have any wallet", redisUser.getUserId());
                return new Results.Result<>(Results.SYSTEM_BUSY, null);
            }
            BigDecimal leftAmount = balanceInfo.getAmount();//用户余额
            if (leftAmount.compareTo(new BigDecimal(amount)) < 0) {
                rlog.error("user wallet left amount:{}->{} is not enough,withdraw amount", leftAmount, amount);
                return new Results.Result<>(Results.Result.SYSTEM_BUSY, "账户余额不足", null);
            }
        }
        return new Results.Result<>(Results.SUCCESS, true);
    }
}
