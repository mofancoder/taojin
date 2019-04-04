package com.tj.util.enums;

/**
 * Created by yelo on 2015/10/28.
 */
public enum RedisKeys {

    USER_TOKEN_INFO("user:token:info:"),
    USER_PHONE_TOKEN("user:phone:token:"),
    USER_CAPTCHA_FORGET_PWD("user:captcha:forget_pwd:"),
    USER_CAPTCHA_FORGET_CPWD("user:captcha:forget_cpwd:"),
    USER_CAPTCHA_MODIFY_PHONE("user:captcha:modify_phone:"),
    USER_CAPTCHA_LOGIN("user:captcha:login:"),
    USER_REGISTER_VALIDATED("user:register:validated:"),
    USER_CAPTCHA_REGISTER("user:captcha:register:"),
    USER_CAPTCHA_MACADDRESS("user:captcha:mac_address:"),
    USER_RISK_WTOKEN("user:disk:wtoken:"),
    USER_RISK_MACADDRESS("user:risk:macAddress:"),
    USER_DISABLE_FAILED_IMG_CAPTCHA("user:disable:failed:img_captcha:"),// 图形验证码错误次数
    USER_DISABLE_FAILED_SMS("user:disable:failed:sms:"),// 短信验证码错误次数
    USER_DISABLE_FAILED_LOGIN("user:disable:failed:login:"), // 登录密错误次数
    USER_DISBALE_FAILED_PAY("user:disable:failed:pay:"), // 交易密码错误次数
    USER_CAPTCHA_CHECKED_BY("user:captcha:checked_by:"), // 自己密码校验通过
    PRE_TREADING("preTrading"),
    PURCHASE_LOCK("purchaseLock"),
    CONTRACT_ADDRESS("contractAddressKey"),
    CHAIN_KEY("blockchain_txlist"),
    USER_CAPTCHA_TIMES("user:captcha:times:"),//获取验证码次数
    COIN_INFO("coininfo:"),
    YESTERDAY_SAC_PRICE("yesterday:back:price"),
    SYS_USER_TOKEN("sysUser:token:info:"),
    EVENT_INFO("event:info:"),//单个赛事信息
    EVENT_HISTORY_BOTH_SIDE("event:history:both:"),
    EVENT_HISTORY_RACE_INFO("event:history:race:"),
    EVENT_TREND("event:trend:"),
    EVENT_DIVE("event:dive:"),
    EVENT_RACE("event:race:"),//赛事信息
    EVENT("event:"),
    RACE_SCORE("bet:race:score:"),//赛事比分
    BET_CANCELLATIONS_MIN("bet:cancellations:min:"),//三分钟内取消次数
    BET_CANCELLATIONS_DAY("bet:cancellations:day:");//一天内取消次数



    private String path;

    RedisKeys(String path) {
        this.path = path;
    }

    public static String getOrderEruptKey(String addr) {
        String key = "withdraw:" + addr;
        return key.toLowerCase();
    }

    //币种信息key
    public static String getCoinInfoKey(String coinName) {
        String key = COIN_INFO.getPath() + coinName;
        return key.toLowerCase();
    }

    //所有币种信息key
    public static String getAllCoinInfoKey() {
        String key = "coininfo:all";
        return key.toLowerCase();
    }


    //投票/赎票key
    public static String getUserVoteOrRedeemKey(Integer userId) {
        String key = "voteOrRedeem:userId:" + userId;
        return key;
    }

    // scanTeamTickets ，userMonTikets快照
    public static String getSnapshotResult(Integer createDate) {
        String key = "SnapshotResult:createDate:" + createDate;
        return key;
    }

    public static String getSysAlamKey() {
        String key = "SysAlam";
        return key;
    }


    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return path;
    }
}
