package com.tj.transaction.bridge;

import com.tj.transaction.dao.UserBalanceInfoMapperEx;
import com.tj.transaction.dao.UserInfoMapper;
import com.tj.transaction.dao.UserTransactionRecdMapper;
import com.tj.transaction.domain.UserInfo;
import com.tj.transaction.domain.UserInfoExample;
import com.tj.transaction.domain.UserTransactionRecd;
import com.tj.transaction.domain.UserTransactionRecdExample;
import com.tj.transaction.service.CommonService;
import com.tj.util.A.FBDException;
import com.tj.util.SacRecdStatusEnum;
import com.tj.util.TransactionTypeEnum;
import com.tj.util.enums.ProxyEnum;
import com.tj.util.enums.otc.SubOrAddEnum;
import com.tj.util.log.Rlog;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: tj-core
 * @description: 交易成功状态处理类
 * @author: liang.song
 * @create: 2018-11-29-11:46
 **/
@Component
public class SuccessTransactionStatus implements TransactionStatus {
    @Resource
    private UserTransactionRecdMapper userTransactionRecdMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserBalanceInfoMapperEx userBalanceInfoMapperEx;
    @Autowired
    private CommonService commonService;
    @Autowired
    private Rlog rlog;

    @Override
    public String code() {
        return SacRecdStatusEnum.success.getCode().toString();
    }

    @Transactional
    @Override
    public void handler(Long transactionId) {
        UserTransactionRecd dbRecd = userTransactionRecdMapper.selectByPrimaryKey(Long.valueOf(transactionId));
        if (dbRecd == null) {
            rlog.error("transaction is not existed:{}", transactionId);
            throw new FBDException("交易失败");
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
            throw new FBDException("交易失败");
        }
        //查找商户
        UserInfoExample userInfoExample = new UserInfoExample();
        userInfoExample.or().andProxyEqualTo(ProxyEnum.sys_merchant.getCode());
        List<UserInfo> userInfos = userInfoMapper.selectByExampleWithRowbounds(userInfoExample, new RowBounds(0, 1));
        if (userInfos.isEmpty()) {
            rlog.error("no system merchant existed");
            throw new FBDException("交易失败");
        }
        UserInfo userInfo = userInfos.get(0);
        Integer merchantId = userInfo.getUserId();//平台商户ID

        switch (TransactionTypeEnum.codeOf(dbRecd.getRecdType())) {
            case charge:
                //先进行平台商户减钱
                //查询平台商户
                int subMerchant = userBalanceInfoMapperEx.subSysMerchantAmount(merchantId, dbRecd.getActualAmount());
                if (subMerchant != 1) {
                    rlog.error("merchant:{} amount not enough", merchantId);
                    throw new FBDException("交易失败");
                }
                //用户加钱
                int add = userBalanceInfoMapperEx.addChargeAmount(dbRecd.getUserId(), dbRecd.getActualAmount());
                if (add != 1) {
                    rlog.error("add user balance amount get wrong,userId:{} ,amount:{}", recd.getUserId(), recd.getActualAmount());
                    throw new FBDException("交易失败");
                }
                try {
                    //插入平台商户积分变动
                    commonService.insertUserBalanceChange(Long.valueOf(transactionId), dbRecd.getActualAmount(), SubOrAddEnum.Sub, merchantId);
                    //插入个体商户积分变动
                    commonService.insertUserBalanceChange(Long.valueOf(transactionId), dbRecd.getActualAmount(), SubOrAddEnum.Add, dbRecd.getUserId());

                } catch (Exception e) {
                    throw new FBDException(e.getLocalizedMessage());
                }
                break;
            case withdraw:
                //提现成功
                //提现成功->更新提现记录状态为成功->扣除冻结金额->插入积分变动记录
                //释放用户冻结金额
                int freeze = userBalanceInfoMapperEx.releaseWithdrawAmount(dbRecd.getUserId(), dbRecd.getAmount());
                if (freeze != 1) {
                    rlog.error("freeze userId:{} ,transactionId:{} ,amount:{} got error", dbRecd.getUserId(), dbRecd.getId(), dbRecd.getActualAmount());
                    throw new FBDException("交易失败");
                }
                //商户加积分
                int addSysMerchantAmount = userBalanceInfoMapperEx.addSysMerchantAmount(merchantId, dbRecd.getAmount());
                if (addSysMerchantAmount != 1) {
                    rlog.error("withdraw add merchant:{} amount:{} got error：", merchantId, dbRecd.getAmount());
                    throw new FBDException("交易失败");
                }

                try {
                    //插入商户积分变动表
                    commonService.insertUserBalanceChange(Long.valueOf(transactionId), dbRecd.getActualAmount(), SubOrAddEnum.Add, merchantId);
                    //插入个体积分变动
                    commonService.insertUserBalanceChange(Long.valueOf(transactionId), dbRecd.getActualAmount(), SubOrAddEnum.Sub, dbRecd.getUserId());
                } catch (Exception e) {
                    throw new FBDException(e.getLocalizedMessage());
                }
                break;
        }
    }
}
