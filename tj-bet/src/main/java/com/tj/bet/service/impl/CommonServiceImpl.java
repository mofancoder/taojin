package com.tj.bet.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.tj.bet.dao.*;
import com.tj.bet.domain.*;
import com.tj.bet.service.CommonService;
import com.tj.util.A.SacException;
import com.tj.util.enums.BetTypeEnum;
import com.tj.util.enums.OptTypeEnum;
import com.tj.util.enums.ProxyEnum;
import com.tj.util.enums.RaceStatusEnum;
import com.tj.util.enums.otc.SubOrAddEnum;
import com.tj.util.log.Rlog;
import com.tj.util.unique.Unique;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-18-14:19
 **/
@Component
public class CommonServiceImpl implements CommonService {
    @Autowired
    private Unique unique;
    @Resource
    private UserBalanceChangeRecdMapper userBalanceChangeRecdMapper;
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

    @Transactional
    public void insertUserBalanceChange(Long transactionId, BigDecimal amount, SubOrAddEnum subOrAddEnum, Integer userId) {
        UserBalanceChangeRecd recd = new UserBalanceChangeRecd();
        Long changeId = unique.nextId();
        recd.setId(changeId);
        recd.setAmount(amount);
        recd.setCreateTime(new Date());
        recd.setOptType(OptTypeEnum.bet.getCode());
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

    @Transactional
    public void insertUserBalanceChange(Long transactionId, BigDecimal amount, SubOrAddEnum subOrAddEnum, Integer userId,Integer optType) {
        UserBalanceChangeRecd recd = new UserBalanceChangeRecd();
        Long changeId = unique.nextId();
        recd.setId(changeId);
        recd.setAmount(amount);
        recd.setCreateTime(new Date());
        recd.setOptType(optType);
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
