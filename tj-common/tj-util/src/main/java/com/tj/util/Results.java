package com.tj.util;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by ldh on 2018-01-30.
 */
@ApiModel
public class Results implements Serializable {
    public static final Result SUCCESS = new Result(Result.SUCCESS, "处理成功");
    public static final Result ACCOUNT_INVALID = new Result(Result.ACCOUNT_INVALID, "账号无效");
    public static final Result NO_REGIST = new Result(Result.NO_REGIST, "未注册");
    public static final Result SEND_FAIL = new Result(Result.SEND_FAIL, "发送失败");
    public static final Result SYSTEM_BUSY = new Result(Result.SYSTEM_BUSY, "系统繁忙");
    public static final Result PARAMETER_INCORRENT = new Result(Result.PARAMETER_INCORRENT, "请求参数有误");
    public static final Result ACCOUNT_EXIST = new Result(Result.ACCOUNT_EXIST, "帐户已注册");
    public static final Result CAPTCHA_INCORRENT = new Result(Result.CAPTCHA_INCORRENT, "验证码错误");
    public static final Result TOKEN_INCORRECT = new Result(Result.TOKEN_INCORRECT, "验证失效，请重新登录");
    public static final Result CAPTCHA_INVALID = new Result(Result.CAPTCHA_INVALID, "验证码失效");
    public static final Result ACCOUNT_NOT_EXIST = new Result(Result.ACCOUNT_NOT_EXIST, "帐号不存在");
    public static final Result REPEAT_SEND = new Result(Result.OP_WAIT, "一分钟内只能请求一次");
    public static final Result ACCOUNT_OR_PASSWORD_WRONG = new Result(Result.ACCOUNT_OR_PASSWORD_WRONG, "帐号或者密码错误");
    public static final Result PASSWORD_WRONG = new Result(Result.PASSWORD_WRONG, "密码错误");
    public static final Result ACCOUNT_IS_PULL_THE_BLACK = new Result(Result.ACCOUNT_IS_PULL_THE_BLACK, "账号已被拉黑");
    public static final Result QUERY_FAILURE = new Result(Result.QUERY_FAILURE, "查询失败");
    public static final Result SAVE_FAILURE = new Result(Result.SAVE_FAILURE, "保存失败");
    public static final Result RECORD_EXISTED = new Result(Result.SAVE_FAILURE, "记录已经存在");
    public static final Result PERCENT_HUNDRED = new Result(Result.SAVE_FAILURE, "收益比率设置不是百分百");
    public static final Result NO_AUTH = new Result(Result.NO_AUTHENTICATION, "认证失败");
    public static final Result NO_AUTHOR = new Result(Result.NO_AUTHORTICATION, "没有权限");
    public static final Result CLOSED = new Result(Result.CLOSED, "未开启");
    public static final Result BetFailed = new Result(Result.BetFailed, "订单校验失败");

    /**
     * 没有访问权限
     */
    public static Result No_Authority = new Result(Result.No_Authority, "没有访问权限");
    public static Result No_MakeUpdate = new Result(Result.No_Authority, "没有修改权限");
    private Map<String, Object> result;

    public Results() {
        this.result = Maps.newHashMap();
    }

    public static Results of() {
        return new Results();
    }

    public Results put(String name, Object value) {
        this.result.put(name, value);
        return this;
    }

    public Map<String, Object> toMap() {
        return this.result;
    }

    @ApiModel
    public static class Result<T> implements Serializable {

        // 处理成功
        public final static int SUCCESS = 200;
        // 系统繁忙
        public final static int SYSTEM_BUSY = -1;
        //未注册
        public final static int NO_REGIST = 3000;
        //发送失败
        public final static int SEND_FAIL = 40000;
        // 请求参数有误
        public final static int PARAMETER_INCORRENT = 40002;
        // 帐户已注册
        public final static int ACCOUNT_EXIST = 40003;
        // 帐号或者密码错误
        public final static int ACCOUNT_OR_PASSWORD_WRONG = 40004;
        //账号无效
        public final static int ACCOUNT_INVALID = -40004;
        //暂无权限
        public final static int No_Authority = -40022;
        // 验证码错误
        public final static int CAPTCHA_INCORRENT = 40007;
        // TOKEN失效
        public final static int TOKEN_INCORRECT = 40008;
        // 账号未注册
        public final static int ACCOUNT_NOT_EXIST = 40009;
        //手机验证码失效
        public final static int CAPTCHA_INVALID = 40010;
        // 1分钟内重复发送
        public final static int REPEAT_SEND = 40011;
        //密码错误
        public final static int PASSWORD_WRONG = 40012;
        //账号已被拉黑
        public final static int ACCOUNT_IS_PULL_THE_BLACK = 40013;
        //账号已在其他设备登录
        public final static int ACCOUNT_OFF_LINE = 40014;
        //地址不存在
        public final static int NOT_FIND_ADDRESS = 40100;
        //库存不足
        public final static int STOCK_NOT_ENOUGH = 50000;
        //找不到
        public final static int NOT_FIND = -50000;
        //没找到订单
        public final static int NOT_FIND_ORDER = 50001;
        //未购买过此商品
        public final static int NOT_BUY_THIS_GOODS = 50002;
        //已经评论过此商品
        public final static int ALREADY_COMMENT = 50003;
        //已经点过赞或者反对
        public final static int ALREADY_PRAISE = 50004;
        //已经收藏过
        public final static int ALREADY_COLLECT = 50005;
        //购买超出限制数量
        public final static int PASS_LIMIT = 50006;
        //已经签到过
        public final static int ALREADY_SIGN_IN = 50007;
        //商品没找到
        public final static int GOODS_NOT_FIND = 60000;
        //不是免费服务商品
        public final static int NOT_SERVICE_GOODS = 60001;
        //用户积分余额不足
        public final static int POINTS_BALANCE_NOT_ENOUGH = 70000;
        //优惠券不可用
        public static final int COUPON_NOT_CAN_USE = 70001;
        //订单不能删除
        public static final int ORDER_NOT_CAN_DELETE = 80000;
        //未获取版本信息
        public static final int NOT_GET_VERSION_INFO = 90000;
        //不在营业日期内
        public static final int BRANCH_NOT_SHOP_DATE = 20001;
        //不在营业时间内
        public static final int BRANCH_NOT_SHOP_TIME = 20002;
        // 操作非法
        public static final int INVALID_OPERRATION = 20004;
        // 无换购活动
        public static final String NOT_EXCHANGE = "NOT_EXCHANGE";
        // 内部数据非法
        public static final int INNER_DATA_INVALID = 20005;
        // 用户当天已参加过活动
        public static final int IS_ADD_EXCHANGE = 20008;
        //换购商品不存在
        public final static int NOT_BUY_EXCHANGE_GOODS = 20006;
        //订单金额不足，不能参该活动
        public final static int NOT_EXCHANGE_GOODS = 20007;
        //修改密码失败
        public final static int RESET_PASSWORD_FAILURE = 40044;
        //获取信息失败
        public static final int GAIN_MSG_FAILURE = 40045;
        //保存失败
        public static final int SAVE_FAILURE = 10001;
        //更新失败
        public static final int UPDATE_FAILURE = 10002;
        //删除失败
        public static final int DELETE_FAILURE = 10003;
        //查询失败
        public static final int QUERY_FAILURE = 10004;
        //生效失败
        public static final int EFFECT_FAILURE = 10005;
        //作废失败
        public static final int CANCEL_FAILURE = 10006;
        //代金卷达不到指定金额
        public static final int COUPON_NOT_ENOUGH_AMOUNT = 10007;

        public static final int UNKNOWN_PAY_WAY = 10008;
        //红包已经抢购完
        public static final int SOLDOUT_FAILURE = 11001;
        //已经抢过红包
        public static final int GRAP_FAILURE = 11002;
        //没有红包活动
        public static final int NO_ACTIVITY = 11003;
        //红包过期
        public static final int OUT_DATE = 11004;
        //当天超过红包抢的次数
        public static final int OUT_LIMIT = 11005;
        //手机号码为空
        public static final int PHONE_NULL = 11006;
        //微信ID为空
        public static final int WXOPENID_NULL = 11007;
        //没有代金券
        public static final int NO_COUPONS = 12000;
        //今日红包使用限制已经过超过3个
        public static final int OUT_USE_LIMIT = 12001;
        //上传广告图片失败
        public static final int UPLOAD_FAIL = 13000;
        //广告比率超过100%
        public static final int OUT_RATIO = 13001;
        // 操作等待
        public final static int OP_WAIT = 13002;
        // 未设置资金密码
        public final static int NOTFUNDPWD = 13003;
        //未设置支付方式
        public final static int NOTBINDPAYWAY = 13004;
        //冻结用户
        public final static int Frozen_User = 13005;
        //授权码错误
        public final static int AUTHCODEERROR = 13006;
        //订单已经存在
        public final static int OrderExist = 13007;
        //需要数据风险
        public final static int NoRisk = 13008;
        //校验短信验证码
        public final static int ValidateMsgCode = 13009;
        //强制更新
        public final static int ForceUpdateApp = 13010;
        //未认证
        public final static int NO_AUTHENTICATION = 14000;
        //无权限
        public final static int NO_AUTHORTICATION = 14001;
        //未开启
        public final static int CLOSED = 15000;
        public final static int TX_FAIL = -9999;
        //订单校验失败
        public final static int BetFailed = 16000;
        //订单取消失败
        public final static int BetCancelFailed = 4001;

        @ApiModelProperty("返回码")
        private int code;
        @ApiModelProperty("返回描述")
        private String msg;
        @JSONField(serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
        @ApiModelProperty("返回结果")
        private T result;
        public Result(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Result(int code, String msg, T result) {
            this.code = code;
            this.msg = msg;
            this.result = result;
        }

        public Result(Result rest, T result) {
            this.code = rest.getCode();
            this.msg = rest.getMsg();
            this.result = result;
        }

        public Result() {
        }

        public T getResult() {
            return result;
        }

        public void setResult(T result) {
            this.result = result;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
