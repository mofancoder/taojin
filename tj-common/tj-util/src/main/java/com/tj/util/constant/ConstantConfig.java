package com.tj.util.constant;

import com.google.common.collect.Maps;
import com.tj.util.enums.CoinIdEnum;
import com.tj.util.enums.UtilConstants;
import com.tj.util.properties.PropertiesUtil;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by ldh on 2018-02-10.
 */
public class ConstantConfig {
    //安币的官网就设成,恒币的官网就设置成恒币
    public static final String WalletAppId = "Wallet";
    public static final String TradeAppId = "TRADE";
    public static final String HengXingStarAppId = PropertiesUtil.getStringValue("hengxing.appId") == null ? "AnbiOTC" : PropertiesUtil.getStringValue("hengxing.appId");
    public static final String MainCoinName = PropertiesUtil.getStringValue("MainCoinName");
    public static final CoinIdEnum MainCoinIdEnum = UtilConstants.getEnumFromName(CoinIdEnum.class, MainCoinName);//CoinIdEnum.SSAC;
    public static final String SuperCoinAddr = PropertiesUtil.getStringValue("blockchain.platform.supercoinaddr");
    public static final String HengxingInGoldurl = PropertiesUtil.getStringValue("hengxing.inGoldurl");
    public static final String HengxingOutGoldurl = PropertiesUtil.getStringValue("hengxing.outGoldurl");
    public static final String UpdateWalletPwdToHengxingUrl = PropertiesUtil.getStringValue("hengxing.updatePwd");
    public static final Long IpSendCaptchaNumber = PropertiesUtil.getLongValue("ip.sendCaptcha.number") == null ? 200 : PropertiesUtil.getLongValue("ip.sendCaptcha.number");
    public static final Long PhoneSendCaptchaNumber = PropertiesUtil.getLongValue("phone.sendCaptcha.number") == null ? 200 : PropertiesUtil.getLongValue("phone.sendCaptcha.number");
    public static final Long sendCaptchaLimitTimeout = PropertiesUtil.getLongValue("sendCaptcha.limit.timeout") == null ? 3600 : PropertiesUtil.getLongValue("sendCaptcha.limit.timeout");
    public static final Long token_outtime = PropertiesUtil.getLongValue("token.outtime");
    public static final Long payTimeout = PropertiesUtil.getLongValue("pay.timeout");
    public static final Long captchaPayTimeout = PropertiesUtil.getLongValue("captcha.pay.timeout");
    public static final Long captchaTimeout = PropertiesUtil.getLongValue("captcha.timeout");
    public static final Integer loginErrorCount = PropertiesUtil.getIntValue("login.error.count");
    public static final Long loginErrorTimeout = PropertiesUtil.getLongValue("login.error.timeout");
    public static final Integer payErrorCount = PropertiesUtil.getIntValue("pay.error.count");
    public static final Long payErrorTimeout = PropertiesUtil.getLongValue("pay.error.timeout");
    public static final Long userTempFrozentimeout = PropertiesUtil.getLongValue("user.temp.frozentimeout");
    public static final Integer OtcMaxDealingCount = PropertiesUtil.getIntValue("otctrade.order.MaxDealingCount");
    public static final BigDecimal OtcMaxMoney = BigDecimal.valueOf(PropertiesUtil.getLongValue("otctrade.order.maxmoney"));
    public static final BigDecimal OtcMaxTotalMoney = BigDecimal.valueOf(PropertiesUtil.getLongValue("otctrade.order.maxTotalMoney"));
    public static final Long thirdAuthCodeTimeout = PropertiesUtil.getLongValue("third.authCode.timeout");
    public static final Integer thirdAuthCodeErrorCount = PropertiesUtil.getIntValue("third.authCode.errorCount");
    public static final String AppId = PropertiesUtil.getStringValue("third.appid");
    public static final String BlockchainWalletReceiveServer = PropertiesUtil.getStringValue("blockchain.wallet.receive.server");
    public static final String BlockchainWalletSendServer = PropertiesUtil.getStringValue("blockchain.wallet.send.server");
    public static final String FundErrorFrozenReason = "FundErrorFrozenReason";
    public static final long RegistUserLimit = PropertiesUtil.getLongValue("regist.user.limit");
    public static final long RegistUserLimitTime = 24 * 60 * 60;
    public static final String BlockchainBtcSendPassword = PropertiesUtil.getStringValue("blockchain.btc.send.password");
    public static final String BlockchainEthSendAddress = PropertiesUtil.getStringValue("blockchain.eth.send.address");
    public static final String BlockchainEthSendPassword = PropertiesUtil.getStringValue("blockchain.eth.send.password");
    public static final String BlockchainSacSendAddress = PropertiesUtil.getStringValue("blockchain.sac.send.address");
    public static final String BlockchainSacSendPassword = PropertiesUtil.getStringValue("blockchain.sac.send.password");
    public static final String BlockchainUsdtSendAddress = PropertiesUtil.getStringValue("blockchain.usdt.send.address");
    public static final String BlockchainUsdtSendPassword = PropertiesUtil.getStringValue("blockchain.usdt.send.password");
    public static final String SuperTradeOperateUser = "12345678901";
    public static final Map<String, String> MapAppidKey = Maps.newHashMap();
    public static final String AliyunSmsProduct = PropertiesUtil.getStringValue("aliyun.sms.product");
    public static final String AliyunSmsDomain = PropertiesUtil.getStringValue("aliyun.sms.domain");
    public static final OwnerConf ownerConf = new OwnerConf();
    public static final String MiningAppId = "Mining";
    public static final String RedEnvelopeAppId = "RedEnvelope";
    public static final String SACFundAppId = "SACFund";
    public static final String InviteAppId = "Invite";
    public static final Integer nickNameModifyNum = PropertiesUtil.getIntValue("nickname.modify.num");
    public static final Long nickNameModifyTime = PropertiesUtil.getLongValue("nickname.modify.timeout");
    public static final String AliyunSmsAccessKeyId = ownerConf.aliyunSmsAccessKeyId;//PropertiesUtil.getStringValue("aliyun.sms.accessKeyId");
    public static final String AliyunSmsAccessKeySecret = ownerConf.aliyunSmsAccessKeySecret;//PropertiesUtil.getStringValue("aliyun.sms.accessKeySecret");
    public static String BaseHost = "http://wallet.hengxingstar.com";
    public static boolean RegistCoinEnable = Boolean.parseBoolean(PropertiesUtil.getStringValue("registCoin.enable"));
    public static String RegistCoinThirdAppId = PropertiesUtil.getStringValue("registCoin.thirdAppId");
    public static BigDecimal RegistCoinCount = new BigDecimal(PropertiesUtil.getStringValue("registCoin.count"));
    public static BigDecimal traInAmountToRmbLimitMax = new BigDecimal(PropertiesUtil.getStringValue("transferInWallet.amount.toRmbLimitMax"));
    public static BigDecimal traOutAmountToRmbLimitMin = new BigDecimal(PropertiesUtil.getStringValue("transferOutWallet.amount.toRmbLimitMin"));
    public static BigDecimal registCoinTotalAmount = new BigDecimal(PropertiesUtil.getStringValue("registCoin.totalAmount"));
    public static BigDecimal WithdrawEachLimitAmount = new BigDecimal(PropertiesUtil.getStringValue("withdraw.each.limitAmount"));
    public static BigDecimal WithdrawDayiLyLimitTotalAmount = new BigDecimal(PropertiesUtil.getStringValue("withdraw.dayily.limitTotalAmount"));
    public static BigDecimal InviteMinBTC = new BigDecimal(PropertiesUtil.getStringValue("Invite.minBTC"));
    public static BigDecimal InviteMinETH = new BigDecimal(PropertiesUtil.getStringValue("Invite.minETH"));
    public static BigDecimal InviteMinSAC = new BigDecimal(PropertiesUtil.getStringValue("Invite.minSAC"));
    public static String sysUserLoginIp = PropertiesUtil.getStringValue("sys.login.ip") == null ? "" : PropertiesUtil.getStringValue("sys.login.ip");
    public static Integer sysErrorLoginCount = PropertiesUtil.getIntValue("sys.errorLogin.count") == null ? 10 : PropertiesUtil.getIntValue("sys.errorLogin.count");
    public static String HengjiuWebUrl = PropertiesUtil.getStringValue("hengjiu.webUrl");
    public static String aliyunRiskEnable = PropertiesUtil.getStringValue("aliyun.risk.enable");
    public static String rewardSacUrl = PropertiesUtil.getStringValue("reward.sac.url");
    public static String localIp = PropertiesUtil.getStringValue("local.ip");
    public static boolean ApplicationTest = false;

    static {
        String applicationEnvironment = PropertiesUtil.getStringValue("application.environment");
        if (applicationEnvironment != null && applicationEnvironment.equals("test")) {
            ApplicationTest = true;
        }

        if (!ApplicationTest) {
            BaseHost = "https://www.sacbox.net";
        } else {
            HengjiuWebUrl = "http://pages.test.sacbox.net/sac_course.html";
            rewardSacUrl = "http://pages.test.sacbox.net/sac_course.html";
        }
    }

    static {
        MapAppidKey.put("WTG", "cfabc118ba000923b36420c35b95a400");//盛世环球
        MapAppidKey.put("XCOQ", "9886ad5525c8dee61ffc28b9bd893db0");//爱客金融
        MapAppidKey.put("TAHOE", "871dae205589cfbe40a7984c22289e90");
        MapAppidKey.put("BallBetting", "871dae205589cfbe40a7984c22289e72");
        MapAppidKey.put("Wallet", "871dae205589cfbe40a7984c22289e89");
        ownerConf.init(AppId);
    }

    public static class OwnerConf {
        String appId;
        String coinName;
        String webSiteName;
        String aliyunSmsAccessKeyId;
        String aliyunSmsAccessKeySecret;
        String financePhone;

        String captchaTemplateName;
        String sendOnModFundPwd;
        String sendOnDifCityLogin;
        String sendToBuyerOnLetgo;
        String sendToSellerOnPayed;

        String sendToBuyerOnAppealBuyWin;
        String sendToSellerOnAppealBuyWin;
        String sendToBuyerOnAppealBuyFail;
        String sendToSellerOnAppealBuyFail;

        private void setWTGTemplateName() {//恒永网
            coinName = "TGBC";
            webSiteName = "恒永网";
            aliyunSmsAccessKeyId = "LTAIQKNrFcLLshl2";
            aliyunSmsAccessKeySecret = "JAq1vE0jvCitLywmClXExgCazkwkyQ";
            captchaTemplateName = "SMS_127167927";
            sendOnModFundPwd = "SMS_127159911";
            sendOnDifCityLogin = "SMS_127159923";
            sendToBuyerOnLetgo = "SMS_127169873";
            sendToSellerOnPayed = "SMS_127159915";
            sendToBuyerOnAppealBuyWin = "";
            sendToSellerOnAppealBuyWin = "";
            sendToBuyerOnAppealBuyFail = "";
            sendToSellerOnAppealBuyFail = "";
        }

        private void setXCOQTemplateName() {//恒星网
            coinName = "QTBC";
            webSiteName = "恒星网";//LTAIDjeoaPVIzOnv
            aliyunSmsAccessKeyId = "LTAIDjeoaPVIzOnv";
            aliyunSmsAccessKeySecret = "E2bnMWZfZjKAMkUs7EDUkF7qsJbNXJ";
            captchaTemplateName = "SMS_127158367";
            sendOnModFundPwd = "SMS_127154989";
            sendOnDifCityLogin = "SMS_127164972";
            sendToBuyerOnLetgo = "SMS_127164977";
            sendToSellerOnPayed = "SMS_127154993";
            sendToBuyerOnAppealBuyWin = "";
            sendToSellerOnAppealBuyWin = "";
            sendToBuyerOnAppealBuyFail = "";
            sendToSellerOnAppealBuyFail = "";
        }

        private void setTAHOETemplateName() {//恒定网
            coinName = "OEBC";
            webSiteName = "恒定网";
            aliyunSmsAccessKeyId = "LTAIDHDiQLDkwvHq";
            aliyunSmsAccessKeySecret = "da0a6v5WGSs9hVryG2lqCPqG4lCB6K";
            captchaTemplateName = "SMS_127158057";
            sendOnModFundPwd = "SMS_127159913";
            sendOnDifCityLogin = "SMS_127159966";
            sendToBuyerOnLetgo = "SMS_127159924";
            sendToSellerOnPayed = "SMS_127154998";
            sendToBuyerOnAppealBuyWin = "";
            sendToSellerOnAppealBuyWin = "";
            sendToBuyerOnAppealBuyFail = "";
            sendToSellerOnAppealBuyFail = "";
        }

        private void setTestTemplateName() {//测试
            coinName = "SAC";
            webSiteName = "sac盒子";
            aliyunSmsAccessKeyId = "LTAIZApda6ENj2fO";
            aliyunSmsAccessKeySecret = "FXrI3zNqDHWvR8gXYtBNpChO6dbyXs";
            captchaTemplateName = "SMS_123798694";
            sendOnModFundPwd = "SMS_128640011";
            sendOnDifCityLogin = "SMS_127169849";
            sendToBuyerOnLetgo = "SMS_127169857";
            sendToSellerOnPayed = "SMS_127154969";
            sendToBuyerOnAppealBuyWin = "";
            sendToSellerOnAppealBuyWin = "";
            sendToBuyerOnAppealBuyFail = "";
            sendToSellerOnAppealBuyFail = "";
        }

        //配置信息在禅道: http://47.52.128.11:9002/zentao/story-view-2.html
        public void init(String appid) {
            appId = appid;
            switch (appid) {
                case "test":
                case "AnbiOTC":
                case "Wallet":
                    setTestTemplateName();
                    break;
                case "WTG":
                    setWTGTemplateName();
                    break;
                case "XCOQ":
                    setXCOQTemplateName();
                    break;
                case "TAHOE":
                    setTAHOETemplateName();
                    break;
                default:
                    break;
            }
        }

        public String getCoinName() {
            return coinName;
        }

        public String getWebSiteName() {
            return webSiteName;
        }


        public String getFinancePhone() {
            return financePhone;
        }

        public String getSendOnModFundPwd() {
            return sendOnModFundPwd;
        }

        public String getSendOnDifCityLogin() {
            return sendOnDifCityLogin;
        }

        public String getSendToBuyerOnLetgo() {
            return sendToBuyerOnLetgo;
        }

        public String getSendToBuyerOnAppealBuyWin() {
            return sendToBuyerOnAppealBuyWin;
        }

        public String getSendToSellerOnAppealBuyWin() {
            return sendToSellerOnAppealBuyWin;
        }

        public String getSendToBuyerOnAppealBuyFail() {
            return sendToBuyerOnAppealBuyFail;
        }

        public String getSendToSellerOnAppealBuyFail() {
            return sendToSellerOnAppealBuyFail;
        }

        public String getCaptchaTemplateName() {
            return captchaTemplateName;
        }

        public String getAppId() {
            return appId;
        }

        public String getSendToSellerOnPayed() {
            return sendToSellerOnPayed;
        }

    }
}
