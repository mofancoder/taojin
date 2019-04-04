package com.tj.util.enums;

/**
 * Created by ldh on 2018-02-09.
 */
public enum CoinAddrPlatEnum {
    //t_user_coin_addr `platform` tinyint default 1 comment '0-平台托管的,1-安币官网分配的区块链地址,2-用户私人的区块链地址',
    PLAT_OWN,//平台数据库托管的 0
    OFFICALWEB,//官网 1
    USER_PRIVATE, //用户私有的 2
    Wallet,//钱包 3
    OTC//点对点场外交易 4
}
