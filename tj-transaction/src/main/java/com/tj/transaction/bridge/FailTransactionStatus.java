package com.tj.transaction.bridge;

import com.tj.transaction.dao.UserBalanceInfoMapperEx;
import com.tj.transaction.dao.UserInfoMapper;
import com.tj.transaction.dao.UserTransactionRecdMapper;
import com.tj.transaction.domain.UserTransactionRecd;
import com.tj.transaction.domain.UserTransactionRecdExample;
import com.tj.util.A.SacException;
import com.tj.util.SacRecdStatusEnum;
import com.tj.util.TransactionTypeEnum;
import com.tj.util.log.Rlog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @program: tj-core
 * @description: 失败交易状态
 * @author: liang.song
 * @create: 2018-11-29-13:49
 **/
@Component
public class FailTransactionStatus implements TransactionStatus {
    @Resource
    private UserTransactionRecdMapper userTransactionRecdMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserBalanceInfoMapperEx userBalanceInfoMapperEx;
    @Autowired
    private Rlog rlog;


    @Override
    public String code() {
        return String.valueOf(SacRecdStatusEnum.fail.getCode());
    }

    @Transactional
    @Override
    public void handler(Long transactionId) {
        UserTransactionRecd dbRecd = userTransactionRecdMapper.selectByPrimaryKey(Long.valueOf(transactionId));
        if (dbRecd == null) {
            rlog.error("transaction is not existed:{}", transactionId);
            throw new SacException("交易失败");
        }
        //根据code查找对应关系
        Integer recdStatus = Integer.valueOf(code());
        if (dbRecd.getRecdStatus().equals(recdStatus)) {
            rlog.warn("transaction:{} status equal to callbackStatus,no need to update", transactionId);
            return;
        }
        UserTransactionRecdExample example = new UserTransactionRecdExample();
        example.or().andIdEqualTo(Long.valueOf(transactionId)).andRecdStatusEqualTo(SacRecdStatusEnum.processing.getCode());
        UserTransactionRecd recd = new UserTransactionRecd();
        recd.setRecdStatus(recdStatus);
        int update = userTransactionRecdMapper.updateByExampleSelective(recd, example);
        if (update != 1) {
            rlog.error("update transaction recd status got wrong,the update return is not 1");
            throw new SacException("交易失败");
        }
        switch (TransactionTypeEnum.codeOf(dbRecd.getRecdType())) {
            case charge:
                //充值失败:更新状态为失败
                break;
            case withdraw:
                //提现失败:更新状态为失败->回滚冻结金额
                int rollbackAmount = userBalanceInfoMapperEx.rollbackAmount(dbRecd.getUserId(), dbRecd.getAmount());
                if (rollbackAmount != 1) {
                    rlog.error("rollback freazon amount got wrong,tran");
                    throw new SacException("提现失败");
                }
                break;
        }
    }


}
