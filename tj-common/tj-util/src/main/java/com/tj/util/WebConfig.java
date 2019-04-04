package com.tj.util;

import java.io.IOException;
import java.util.Properties;

/**
 * @author yelo
 * @date 2015/10/26
 */
public abstract class WebConfig {
    //环境相关模块
    public static final String ENV_IS_TEST;
    //token过期有效时间(分钟)
    public static final int MAX_REDIS_EXPIRE_TIME = 15 * 24 * 60;
    //请求签名
    public static final String REQUEST_SIGN_KRY = "5892246DC34A4313BE2C608276E64FDF";
    // 短信模块
    public static final String SMS_URL;
    public static final String SMS_API_KEY;
    public static final String SMS_CAPTCHA_TEMPLATE_CODE;
    public static final String SMS_ORDER_TEMPLATE_CODE;
    public static final String SMS_ENCODING;


    public static final String ZFB_CALLBACK_URL;
    public static final String ZFB_APP_AUTH_TOKEN;

    //微信模块
    public static final String WEIXIN_PAY_URL;
    public static final String WEIXIN_APP_ID;
    public static final String WEIXIN_MCH_ID;
    public static final String WEIXIN_API_KEY;
    public static final String WEIXIN_WEB_MCH_ID;
    public static final String WEIXIN_WEB_APP_ID;
    public static final String WEIXIN_WEB_APP_SECRET;
    public static final String WEIXIN_SPBILL_CREATE_IP;
    public static final String WEIXIN_NOTIFY_URL;
    public static final String WEIXIN_DEVICE_INFO;
    public static final String WEIXIN_TRADE_TYPE;
    public static final String WEIXIN_WEB_TRADE_TYPE;
    public static final String WEIXIN_CERT_LOCAL_PATH;
    public static final String WEIXIN_XCX_CERT_LOCAL_PATH;
    public static final String WEIXIN_APP_CERT_LOCAL_PATH;

    public static final String WEIXIN_XCX_TRADE_TYPE;
    public static final String WEIXIN_XCX_MCH_ID;
    public static final String WEIXIN_XCX_APP_ID;

    //默认配置
    public static final String DEFAULT_STORE_ID;
    public static final String DEFAULT_STORE_NO;
    public static final String DEFAULT_USER_ID;

    //文件上传路径
    public static final String DEFAULT_UPLOAD_IMAGE_URL;

    //订单生成路径
    public static final String DEFAULT_ORDER_NO_URL;
    public static final String DEFAULT_PD_NO_URL;
    public static final String DEFAULT_SALE_NO_URL;
    public static final String DEFAULT_CG_NO_URL;
    public static final String DEFAULT_TH_NO_URL;
    public static final String DEFAULT_BS_NO_URL;

    //推送
    public static final String PUSH_APP_KEY;
    public static final String PUSH_MASTER_SECRET;
    //商家版
    public static final String PUSH_APP_KEY_BUSINESS;
    public static final String PUSH_MASTER_SECRET_BUSINESS;
    //配送版
    public static final String PUSH_APP_KEY_MEMBER;
    public static final String PUSH_MASTER_SECRET_MEMBER;
    //安卓
    public static final String PUSH_ANDROID_APP_KEY;
    public static final String PUSH_ANDROID_MASTER_SECRET;
    //商家版
    public static final String PUSH_ANDROID_APP_KEY_BUSINESS;
    public static final String PUSH_ANDROID_MASTER_SECRET_BUSINESS;
    //配送版
    public static final String PUSH_ANDROID_APP_KEY_MEMBER;
    public static final String PUSH_ANDROID_MASTER_SECRET_MEMBER;
    //推送是否为测试模式
    public static final boolean UM_PUSH_IS_DEBUG;
    //是否运行跨域
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN;

    public static final String CZ_SMS_ACCOUNT;
    public static final String CZ_SMS_PASSWORD;
    public static final String CZ_SMS_SALE_ACCOUNT;
    public static final String CZ_SMS_SALE_PASSWORD;
    public static final String CZ_SMS_URL;
    public static final String CZ_SMS_VERIFICATION_TPL;
    public static final String CZ_SMS_REGISTER_TPL;
    public static final String CZ_SMS_REGISTER_TPL_V2;
    public static final String CZ_SMS_HONGBAO_TPL;
    public static final String CZ_SMS_HONGBAO_TPL_V2;
    public static final String CZ_SMS_UNLOCK_TPL;

    public static final String CZ_VOICE_APP_ID;
    public static final String CZ_VOICE_APP_KEY;
    public static final String CZ_VOICE_VERIFICATION_TPL;
    public static final String CZ_VOICE_URL;
    public static final String REDENV_OUTDATE_PUSHTIME;
    public static final String SEVEN_DISCOUNT_AREA_ID;
    public static final String SEVEN_DISCOUNT_BEGINTIME_ENDTIME;

    //store.modify.phones
    public static final String[] STORE_MODIFY_PHONES;
    public static final String ADRESS_LABEL;
    //订单标签
    public static final String ORDER_LABEL;
    //开锁
    public static final String LOCK_UPDATE_CUSTOMER_STATUS_URL;  //修改开锁客服在线状态URL

    public static final String DISABLE_REGISTER_COUPON_STORES; //取消注册代金券使用的店铺ID

    //联通沃支付红包兑换面额（格式：面额_couponId,面额_couponId,面额_couponId....）
    public static final String UNION_EPAY_COUPON_AMOUNT;


    public static final String[] sortedCitys = {"惠州市", "广州市", "深圳市", "佛山市", "东莞市", "宁波市",
            "汕头市", "茂名市", "湛江市", "厦门市", "长沙市", "中山市", "江门市",
            "南昌市"
//            ,"广交会"
            , "河源市", "汕尾市", "重庆市", "武汉市"};

    static {
        try {
            Properties prop = new Properties();
            prop.load(WebConfig.class.getResourceAsStream("/config.properties"));
            ENV_IS_TEST = prop.getProperty("env.is_test", "0");
            SMS_URL = prop.getProperty("sms.url");
            SMS_API_KEY = prop.getProperty("sms.api_key");
            SMS_CAPTCHA_TEMPLATE_CODE = prop.getProperty("sms.captcha_template_code");
            SMS_ORDER_TEMPLATE_CODE = prop.getProperty("sms.order_template_code");
            SMS_ENCODING = prop.getProperty("sms.encoding");

            ZFB_CALLBACK_URL = prop.getProperty("zfb.callback_url");
            ZFB_APP_AUTH_TOKEN = prop.getProperty("zfb.app_auth_token");

            WEIXIN_PAY_URL = prop.getProperty("wx.pay_url");
            WEIXIN_APP_ID = prop.getProperty("wx.app_id");
            WEIXIN_WEB_APP_SECRET = prop.getProperty("wx.web_app_secret");
            WEIXIN_MCH_ID = prop.getProperty("wx.mch_id");
            WEIXIN_API_KEY = prop.getProperty("wx.api_key");
            WEIXIN_WEB_MCH_ID = prop.getProperty("wx.web_mch_id");
            WEIXIN_WEB_APP_ID = prop.getProperty("wx.web_app_id");
            WEIXIN_SPBILL_CREATE_IP = prop.getProperty("wx.spbill_create_ip");
            WEIXIN_NOTIFY_URL = prop.getProperty("wx.notify_url");
            WEIXIN_DEVICE_INFO = prop.getProperty("wx.device_info");
            WEIXIN_TRADE_TYPE = prop.getProperty("wx.trade_type");
            WEIXIN_WEB_TRADE_TYPE = prop.getProperty("wx.web_trade_type");
            WEIXIN_CERT_LOCAL_PATH = prop.getProperty("wx.cert_local_path");
            WEIXIN_XCX_CERT_LOCAL_PATH = prop.getProperty("wx.xcx.cert_local_path");
            WEIXIN_APP_CERT_LOCAL_PATH = prop.getProperty("wx.app.cert_local_path");


            WEIXIN_XCX_TRADE_TYPE = prop.getProperty("wx.xcx_trade_type");
            WEIXIN_XCX_MCH_ID = prop.getProperty("wx.xcx_mch_id");
            WEIXIN_XCX_APP_ID = prop.getProperty("wx.xcx_app_id");

            DEFAULT_STORE_ID = prop.getProperty("default_store_id");
            DEFAULT_USER_ID = prop.getProperty("default_user_id");
            DEFAULT_STORE_NO = prop.getProperty("default_store_no");

            DEFAULT_UPLOAD_IMAGE_URL = prop.getProperty("default_upload_image_url");

            DEFAULT_ORDER_NO_URL = prop.getProperty("default_order_no_url");
            DEFAULT_PD_NO_URL = prop.getProperty("default_pd_no_url");
            DEFAULT_SALE_NO_URL = prop.getProperty("default_sales_no_url");
            DEFAULT_CG_NO_URL = prop.getProperty("default_cg_no_url");
            DEFAULT_BS_NO_URL = prop.getProperty("default_bs_no_url");
            DEFAULT_TH_NO_URL = prop.getProperty("default_th_no_url");
            //推送ios
            PUSH_APP_KEY = prop.getProperty("push.app_key");
            PUSH_MASTER_SECRET = prop.getProperty("push.master_secret");
            PUSH_APP_KEY_BUSINESS = prop.getProperty("push.app_key_business");
            PUSH_MASTER_SECRET_BUSINESS = prop.getProperty("push.master_secret_business");
            PUSH_APP_KEY_MEMBER = prop.getProperty("push.app_key_member");
            PUSH_MASTER_SECRET_MEMBER = prop.getProperty("push.master_secret_member");
            //推送安卓
            PUSH_ANDROID_APP_KEY = prop.getProperty("push.android.app_key");
            PUSH_ANDROID_MASTER_SECRET = prop.getProperty("push.android.master_secret");
            PUSH_ANDROID_APP_KEY_BUSINESS = prop.getProperty("push.android.app_key_business");
            PUSH_ANDROID_MASTER_SECRET_BUSINESS = prop.getProperty("push.android.master_secret_business");
            PUSH_ANDROID_APP_KEY_MEMBER = prop.getProperty("push.android.app_key_member");
            PUSH_ANDROID_MASTER_SECRET_MEMBER = prop.getProperty("push.android.master_secret_member");
            //推送是否为测试模式
            UM_PUSH_IS_DEBUG = "true".equals(prop.getProperty("push.is_debug", "false"));
            //跨域设置
            ACCESS_CONTROL_ALLOW_ORIGIN = prop.getProperty("access_control_allow_origin");
            //畅卓短信平台
            CZ_SMS_ACCOUNT = prop.getProperty("cz.sms.account");
            CZ_SMS_PASSWORD = prop.getProperty("cz.sms.pwd");
            CZ_SMS_SALE_ACCOUNT = prop.getProperty("cz.sms.sale.account");
            CZ_SMS_SALE_PASSWORD = prop.getProperty("cz.sms.sale.pwd");
            CZ_SMS_URL = prop.getProperty("cz.sms.url");
            CZ_SMS_VERIFICATION_TPL = prop.getProperty("cz.sms.verify.tpl");
            CZ_SMS_REGISTER_TPL = prop.getProperty("cz.sms.register.tpl");
            CZ_SMS_REGISTER_TPL_V2 = prop.getProperty("cz.sms.register.tpl.v2");
            CZ_SMS_HONGBAO_TPL = prop.getProperty("cz.sms.hongbao.tpl");
            CZ_SMS_HONGBAO_TPL_V2 = prop.getProperty("cz.sms.hongbao.tpl.v2");
            CZ_SMS_UNLOCK_TPL = prop.getProperty("cz.sms.unlock.tpl");

            CZ_VOICE_APP_ID = prop.getProperty("cz.voice.app.id");
            CZ_VOICE_APP_KEY = prop.getProperty("cz.voice.app.key");
            CZ_VOICE_VERIFICATION_TPL = prop.getProperty("cz.voice.verify.tpl");
            CZ_VOICE_URL = prop.getProperty("cz.voice.url");
            REDENV_OUTDATE_PUSHTIME = prop.getProperty("redenv.outdate.pushtime");
            SEVEN_DISCOUNT_AREA_ID = prop.getProperty("seven.discount.area.id");
            SEVEN_DISCOUNT_BEGINTIME_ENDTIME = prop.getProperty("seven.discount.begintime.endtime");
            LOCK_UPDATE_CUSTOMER_STATUS_URL = prop.getProperty("lock.update.customer.status.url");
            DISABLE_REGISTER_COUPON_STORES = prop.getProperty("disable.register.coupon.stores");
            UNION_EPAY_COUPON_AMOUNT = prop.getProperty("union_epay_coupon_amount");

            ADRESS_LABEL = prop.getProperty("address_label");
            ORDER_LABEL = prop.getProperty("order_label");
            String phones = prop.getProperty("store.modify.phones", "");
            if (!"".equals(phones)) {
                STORE_MODIFY_PHONES = phones.split(",");
            } else {
                STORE_MODIFY_PHONES = new String[0];
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("无法加载config.properties");
        }
    }

    /**
     * 根据环境返回哪个字符串
     *
     * @param sInTest    如果是测试环境返回测试用的字符串
     * @param sInProduct 如果是正式环境返回正式用的字符串
     * @return
     */
    static public String retByEnv(String sInTest, String sInProduct) {
        if ("1".equals(ENV_IS_TEST)) {
            return sInTest;
        }
        return sInProduct;
    }

    /**
     * 是否测试环境
     *
     * @return
     */
    static public boolean isTestEvn() {
        return "1".equals(ENV_IS_TEST);
    }
}
