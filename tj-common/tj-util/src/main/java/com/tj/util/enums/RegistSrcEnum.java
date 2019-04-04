package com.tj.util.enums;

/**
 * Created by ldh on 2018-03-21.
 */
public enum RegistSrcEnum {
    //alter table t_customer_info add column regist_src tinyint unsigned not null default 1 comment '0-未知,1-钱包,2-交易所,3-球类预测,4-点对点,参考 RegistSrcEnum';

    Unknown,
    Wallet,
    Exchange,
    BallBetting,
    Peer2Peer
}
