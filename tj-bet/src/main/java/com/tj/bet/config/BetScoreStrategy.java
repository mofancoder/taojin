package com.tj.bet.config;

import com.tj.bet.dao.*;
import com.tj.bet.domain.*;
import com.tj.bet.service.CommonService;
import com.tj.dto.BetScoreDto;
import com.tj.util.A.SacException;
import com.tj.util.enums.*;
import com.tj.util.enums.otc.SubOrAddEnum;
import com.tj.util.settle.SettleRule;
import com.tj.util.unique.Unique;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-13-13:43
 **/
@Component
@Slf4j
public class BetScoreStrategy extends BetStrategy<BetScoreDto> {
    @Resource
    private RaceInfoMapper raceInfoMapper;
    @Resource
    private BetRecdMapper betRecdMapper;
    @Resource
    private RaceRebateInfoMapper raceRebateInfoMapper;
    @Resource
    private BetRecdMapperEx betRecdMapperEx;
    @Resource
    private UserBalanceInfoMapperEx userBalanceInfoMapperEx;

    @Resource
    private UserInfoMapper userInfoMapper;
    private final CommonService commonService;
    private final Unique unique;
    @Resource
    private RebateRecdMapper rebateRecdMapper;

    @Autowired
    public BetScoreStrategy(CommonService commonService, Unique unique) {
        this.commonService = commonService;
        this.unique = unique;
    }

    @Override
    public BetTypeEnum type() {
        return BetTypeEnum.score;
    }

    /**
     * 结算
     * 1.根据投注内容查找比赛
     * 2.根据比赛结果查找对应的返利率
     * 3.根据投注的内容的比分 vs  赛事结果比分 ->
     * bet: 比分 返利率  投注
     * 1:0   15%   $100
     * result:
     * 比分   输赢   收益
     * 1:0    lose   0
     * not1:0   赢    100+100*15%=115
     * 4.赢->进行用户加钱—>积分变动
     * 5.标记投注状态输赢状态
     * 6. 4&5是一个事务
     *
     * @param content
     */
    @Override
    @Transactional
    public void settle(String content) {

        //查找商户
        Integer merchantId = selectMerchantId();//平台商户ID
        BetScoreDto serialize = serialize(content);
        String raceId = serialize.getRaceId();
        String betId = serialize.getBetId();
        //比赛
        RaceInfo raceInfo = raceInfoMapper.selectByPrimaryKey(raceId);
        if (raceInfo == null) {
            log.error("race  is not existed:{}", raceId);
            return;
        }
        //判断是否已经结束
        Date endTime = raceInfo.getEndTime();
        Integer raceStatus = raceInfo.getRaceStatus();
        if (!raceStatus.equals(RaceStatusEnum.end.getCode()) || endTime == null) {
            return;
        }
        //查找返利率
        String score = serialize.getScore();//投注的比分
        String finalResult = raceInfo.getWinResult();//游戏结果
        boolean win = false;
        //判断输赢
        if (!SettleRule.equalsNumber(finalResult, score)) {
            win = true;
            //用户加钱
            int addUserAmount = userBalanceInfoMapperEx.addRebateAmount(serialize.getUserId(), serialize.getExpecAmount());
            if (addUserAmount != 1) {
                log.error("add user:{} rebate amount:{} got error", serialize.getUserId(), serialize.getExpecAmount());
                throw new SacException("add user:" + serialize.getUserId() + " amount:" + serialize.getExpecAmount() + "got error");
            }
            //商户减钱
            int subSysAmount = userBalanceInfoMapperEx.cutBetAmount(merchantId, serialize.getExpecAmount());
            if (subSysAmount != 1) {
                log.error("add merchant:{} rebate amount:{} got error", merchantId, serialize.getExpecAmount());
                throw new SacException("add merchant:" + merchantId + " amount:" + serialize.getExpecAmount() + "got error");
            }
            //用户积分变动
            commonService.insertUserBalanceChange(Long.valueOf(betId), serialize.getExpecAmount(), SubOrAddEnum.Add, serialize.getUserId(),OptTypeEnum.rebate.getCode());
            //商户积分变动
            commonService.insertUserBalanceChange(Long.valueOf(betId), serialize.getExpecAmount(), SubOrAddEnum.Sub, merchantId,OptTypeEnum.rebate.getCode());
            //插入返利记录
            insertRebateRecd(serialize);

        }
        //更新投注输赢状态
        BetRecd betRecd = new BetRecd();
        betRecd.setRebateStatus(RebateStatusEnum.success.getCode());
        betRecd.setBetResult(win ? BetResultEnum.win.getCode() : BetResultEnum.lose.getCode());
        BetRecdExample example = new BetRecdExample();
        example.or()
                .andIdEqualTo(Long.valueOf(betId))
                .andRebateStatusEqualTo(RebateStatusEnum.un_rebate.getCode())
                .andBetResultIsNull();
        int updateStatus = betRecdMapper.updateByExampleSelective(betRecd, example);
        if (updateStatus != 1) {
            throw new RuntimeException("update bet rebate status got error");
        }


    }

    /**
     * 结算
     * 1.根据投注内容解析出投注比分和其他信息
     * 2.根据传入的比分和投注比分按照最新结算规则进行对比
     * 3.如果投注的比分符合最新的结算规则，则进行返利操作
     * bet: 比分 返利率  投注
     * 1:0   15%   $100
     * result:
     * 比分   输赢   收益
     * 1:0    lose   0
     * not1:0   赢    100+100*15%=115
     * 4.赢->进行用户加钱—>积分变动
     * 5.标记投注状态输赢状态
     * 6. 3&4&5是一个事务
     *
     * @param content
     */
    @Override
    @Transactional
    public void newSettle(String content, String scoreActual) {
        if (StringUtils.isEmpty(scoreActual)) {
            log.error("race score is not null:{}", scoreActual);
            return;
        }
        //平台商户ID
        Integer merchantId = selectMerchantId();
        //获取投注JSON内容
        BetScoreDto betContent = serialize(content);
        String raceId = betContent.getRaceId();
        String betId = betContent.getBetId();
        //投注的比分
        String betScore = betContent.getScore().trim();
        if (StringUtils.isEmpty(betScore)) {
            log.error("bet race score is not null:{}", betScore);
            return;
        }

        //比赛
        RaceInfo raceInfo = raceInfoMapper.selectByPrimaryKey(raceId);
        if (raceInfo == null) {
            log.error("race is not existed:{}", raceId);
            return;
        }
        //赛事状态
        Integer raceStatus = raceInfo.getRaceStatus();
        if (raceStatus != RaceStatusEnum.processing.getCode()) {
            log.error("race is not over:{}", raceId);
            return;
        }
        //判断输赢
        boolean win = SettleRule.isWin(scoreActual, betScore);
        if (win) {
            //用户加钱
            int addUserAmount = userBalanceInfoMapperEx.addRebateAmount(betContent.getUserId(), betContent.getExpecAmount());
            if (addUserAmount != 1) {
                log.error("add user:{} rebate amount:{} got error", betContent.getUserId(), betContent.getExpecAmount());
                throw new SacException("add user:" + betContent.getUserId() + " amount:" + betContent.getExpecAmount() + "got error");
            }
            //商户减钱
            int subSysAmount = userBalanceInfoMapperEx.cutBetAmount(merchantId, betContent.getExpecAmount());
            if (subSysAmount != 1) {
                log.error("add merchant:{} rebate amount:{} got error", merchantId, betContent.getExpecAmount());
                throw new SacException("add merchant:" + merchantId + " amount:" + betContent.getExpecAmount() + "got error");
            }
            //用户积分变动
            commonService.insertUserBalanceChange(Long.valueOf(betId), betContent.getExpecAmount(), SubOrAddEnum.Add, betContent.getUserId(), OptTypeEnum.rebate.getCode());
            //商户积分变动
            commonService.insertUserBalanceChange(Long.valueOf(betId), betContent.getExpecAmount(), SubOrAddEnum.Sub, merchantId, OptTypeEnum.rebate.getCode());
            //插入返利记录
            insertRebateRecd(betContent);
            //更新投注输赢状态
            BetRecd betRecd = new BetRecd();
            betRecd.setRebateStatus(RebateStatusEnum.success.getCode());
            betRecd.setBetResult(win ? BetResultEnum.win.getCode() : BetResultEnum.lose.getCode());
            BetRecdExample example = new BetRecdExample();
            example.or()
                    .andIdEqualTo(Long.valueOf(betId))
                    .andRebateStatusEqualTo(RebateStatusEnum.un_rebate.getCode())
                    .andBetResultIsNull();
            int updateStatus = betRecdMapper.updateByExampleSelective(betRecd, example);
            if (updateStatus != 1) {
                throw new RuntimeException("update bet rebate status got error");
            }
        }
    }

    /**
     * 定时搜索数据库中取消的赛事
     */
    @Override
    public void selectAllCancelRace(){
        //找出所有取消赛事
        RaceInfoExample exampleRace = new RaceInfoExample();
        exampleRace.or().andRaceStatusEqualTo(RaceStatusEnum.cancel.getCode());
        List<RaceInfo> listRace = raceInfoMapper.selectByExample(exampleRace);
        //当数据库中有取消赛事时
        if(listRace.size()>0){
            for(RaceInfo race:listRace){
                //commonService.rollbackForCancelRace(race.getId());
            }

        }
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

       /* //当数据库中有取消赛事时
        if(!StringUtil.isEmpty(raceId)){
            //根据赛事ID从投注记录表中找出所有投注信息
            BetRecdExample exampleBet = new BetRecdExample();
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
                    log.error("update raceStatus:{} got error", raceId);
                    throw new SacException("update raceStatus:" + raceId + "got error");
                }
            }
        }*/
    }
    /**
     * 1.传入需要返还的用户信息
     * 2.用户金额增加，商户金额减少
     */
    /*
    private void rollbackMoney(List<BetRecd> listBet){
        Integer merchantId = selectMerchantId();
        for(BetRecd betRecd:listBet){
            //返还用户投注金额
            int addUserAmount = userBalanceInfoMapperEx.addRebateAmount(betRecd.getUserId(), betRecd.getBetAmount());
            //将对应投注状态改为未投注(0)
            BetRecd bean = new BetRecd();
            bean.setId(betRecd.getId());
            bean.setBetStatus(BetTypeEnum.bet_cancel.getCode());
            int updateBetStatus = betRecdMapper.updateByPrimaryKeySelective(bean);
            if (addUserAmount != 1||updateBetStatus != 1) {
                log.error("add user:{} rebate amount:{} got error", betRecd.getUserId(), betRecd.getBetAmount());
                throw new SacException("add user:" + betRecd.getUserId() + " amount:" + betRecd.getBetAmount() + "got error");
            }
            //商户减钱
            int subSysAmount = userBalanceInfoMapperEx.cutBetAmount(merchantId, betRecd.getBetAmount());
            if (subSysAmount != 1) {
                log.error("add merchant:{} rebate amount:{} got error", merchantId, betRecd.getBetAmount());
                throw new SacException("add merchant:" + merchantId + " amount:" + betRecd.getBetAmount() + "got error");
            }
        }
    }*/
    /**
     * 查找商户ID
     */
    private Integer selectMerchantId() {
        //查找商户
        UserInfoExample userInfoExample = new UserInfoExample();
        userInfoExample.or().andProxyEqualTo(ProxyEnum.sys_merchant.getCode());
        List<UserInfo> userInfos = userInfoMapper.selectByExampleWithRowbounds(userInfoExample, new RowBounds(0, 1));
        if (userInfos.isEmpty()) {
            log.error("no system merchant existed");
            throw new SacException("交易失败,不存在商户");
        }
        UserInfo userInfo = userInfos.get(0);
        Integer merchantId = userInfo.getUserId();//平台商户ID
        return merchantId;
    }

    /**
     * 插入返利记录
     *
     * @param serialize
     */
    private void insertRebateRecd(BetScoreDto serialize) {
        RebateRecd rebateRecd = new RebateRecd();
        rebateRecd.setId(unique.nextId());
        rebateRecd.setCreateTime(new Date());
        rebateRecd.setRebateAmount(serialize.getExpecAmount());
        rebateRecd.setRebateStatus(RebateStatusEnum.success.getCode());
        rebateRecd.setRelatedBetId(Long.valueOf(serialize.getBetId()));
        rebateRecd.setUserId(serialize.getUserId());
        int insert = rebateRecdMapper.insertSelective(rebateRecd);
        if (insert != 1) {
            throw new SacException("结算失败,插入返利记录失败");
        }

    }
}
