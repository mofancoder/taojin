package com.tj.bet.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.tj.bet.dao.*;
import com.tj.bet.domain.*;
import com.tj.bet.service.CommonService;
import com.tj.bet.service.RollbackService;
import com.tj.util.A.SacException;
import com.tj.util.enums.BetTypeEnum;
import com.tj.util.enums.OptTypeEnum;
import com.tj.util.enums.ProxyEnum;
import com.tj.util.enums.RaceStatusEnum;
import com.tj.util.enums.otc.SubOrAddEnum;
import com.tj.util.log.Rlog;
import com.tj.util.unique.Unique;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
@Component
public class RollbackServiceImpl implements RollbackService {
    @Autowired
    private Rlog rlog;
    @Resource
    private RaceInfoMapper raceInfoMapper;
    @Resource
    private BetRecdMapper betRecdMapper;
    @Resource
    private UserBalanceInfoMapperEx userBalanceInfoMapperEx;
    @Resource
    private UserInfoMapper userInfoMapper;
    private final CommonService commonService;
    private final Unique unique;
    @Autowired
    public RollbackServiceImpl(Unique unique,CommonService commonService){
        this.unique = unique;
        this.commonService = commonService;
    }

    /**
     * 取消赛事回滚金额
     * 1.从赛事表中找出所有取消的赛事race_status=0，获取赛事ID
     * 2.通过赛事ID从投注记录表中找出所有投注记录
     * 3.找出所有投注了该赛事的用户
     * 4.将该用户下注金额返还
     * 5.将投注记录表中投注状态改为未投注(bet_status=0)
     * 6.对应赛事表中将对应数据状态改变
     * @param raceId
     * 参数为赛事ID
     */
    @Override
    @Transactional
    public void rollbackForCancelRace(String raceId) {

        //当数据库中有取消赛事时
        if(!StringUtil.isEmpty(raceId)){
            //根据赛事ID从投注记录表中找出所有投注信息
            BetRecdExample exampleBet = new BetRecdExample();
            //加上赛事ID和投注状态判断
            exampleBet.or().andRaceIdEqualTo(raceId).andBetStatusEqualTo(BetTypeEnum.bet_normal.getCode());
            List<BetRecd> listBet = betRecdMapper.selectByExample(exampleBet);
            //如果投注记录表中有对应数据，则进行下一步操作
            if(listBet.size()>0){
                rollbackMoney(listBet);
                //将赛事状态改为取消成功
                RaceInfo raceInfo = new RaceInfo();
                raceInfo.setId(raceId);
                raceInfo.setRaceStatus(RaceStatusEnum.cancel.getCode());
                int updateRaceStatus = raceInfoMapper.updateByPrimaryKeySelective(raceInfo);
                if (updateRaceStatus != 1) {
                    rlog.error("update raceStatus:{} got error", raceId);
                    throw new SacException("update raceStatus:" + raceId + "got error");
                }
            }
        }
    }
    /**
     * 1.传入需要返还的用户信息
     * 2.用户金额增加，商户金额减少
     */
    private void rollbackMoney(List<BetRecd> listBet){
        Integer merchantId = selectMerchantId();
        for(BetRecd betRecd:listBet){
            //金额
            BigDecimal betAmount = betRecd.getBetAmount();
            //返还用户投注金额
            int addUserAmount = userBalanceInfoMapperEx.addRebateAmount(betRecd.getUserId(), betAmount);
            //将对应投注状态改为未投注(0)
            BetRecd bean = new BetRecd();
            bean.setId(betRecd.getId());
            bean.setBetStatus(BetTypeEnum.bet_cancel.getCode());
            int updateBetStatus = betRecdMapper.updateByPrimaryKeySelective(bean);
            if (addUserAmount != 1||updateBetStatus != 1) {
                rlog.error("add user:{} rebate amount:{} got error", betRecd.getUserId(), betAmount);
                throw new SacException("add user:" + betRecd.getUserId() + " amount:" + betAmount + "got error");
            }
            //商户减钱
            int subSysAmount = userBalanceInfoMapperEx.cutBetAmount(merchantId, betAmount);
            if (subSysAmount != 1) {
                rlog.error("add merchant:{} rebate amount:{} got error", merchantId, betAmount);
                throw new SacException("add merchant:" + merchantId + " amount:" + betAmount + "got error");
            }

            Long betId = unique.nextId();
            //插入商户积分变动
            commonService.insertUserBalanceChange(betId, betAmount, SubOrAddEnum.Sub, merchantId, OptTypeEnum.cancel_bet.getCode());
            //插入个人积分变动
            commonService.insertUserBalanceChange(betId, betAmount, SubOrAddEnum.Add, betRecd.getUserId(),OptTypeEnum.cancel_bet.getCode());
        }
    }
    /**
     * 查找商户ID
     */
    private Integer selectMerchantId(){
        //查找商户
        UserInfoExample userInfoExample = new UserInfoExample();
        userInfoExample.or().andProxyEqualTo(ProxyEnum.sys_merchant.getCode());
        List<UserInfo> userInfos = userInfoMapper.selectByExampleWithRowbounds(userInfoExample, new RowBounds(0, 1));
        if (userInfos.isEmpty()) {
            rlog.error("no system merchant existed");
            throw new SacException("交易失败,不存在商户");
        }
        UserInfo userInfo = userInfos.get(0);
        Integer merchantId = userInfo.getUserId();//平台商户ID
        return merchantId;
    }
}
