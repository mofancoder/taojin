package com.tj.transaction.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tj.dto.RemoteChargeResult;
import com.tj.dto.RemoteWithdrawResult;
import com.tj.transaction.dao.UserTransactionRecdMapper;
import com.tj.transaction.domain.UserTransactionRecd;
import com.tj.util.A.SacException;
import com.tj.util.FBDSignUtil;
import com.tj.util.enums.ChannelCodeType;
import com.tj.util.enums.PlatformTypeEnum;
import com.tj.util.enums.TerminalTypeEnum;
import com.tj.util.log.Rlog;
import com.tj.util.unique.Unique;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @program: tj-core
 * @description: 支付宝交易策略
 * @author: liang.song
 * @create: 2018-11-28-11:04
 **/
@Component
public class AlipayStrategy extends AbstractTransactionStrategy {
    private final Rlog rlog;
    private final RestTemplate restTemplate;
    private final Unique unique;
    @Value("${transaction.app-id}")
    private String appId;
    @Value("${transaction.app-secret}")
    private String appSecret;
    @Value("${transaction.alipay-charge-url}")
    private String chargeUrl;
    private static final String APP_VERSION = "1.6";
    @Value("${transaction.alipay-withdraw-url}")
    private String withdrawUrl;
    @Value("${transaction.notify-url}")
    private String notifyUrl;
    @Resource
    private UserTransactionRecdMapper userTransactionRecdMapper;

    @Autowired
    public AlipayStrategy(Rlog rlog, RestTemplate restTemplate, Unique unique) {
        this.rlog = rlog;
        this.restTemplate = restTemplate;
        this.unique = unique;
    }

    @Override
    public PlatformTypeEnum type() {
        return PlatformTypeEnum.alipay;
    }

    @Override
    public RemoteWithdrawResult withdraw(Long transactionId) {
//        rlog.debug("alipay withdraw start:{} ...........", transactionId);
//        UserTransactionRecd recd = userTransactionRecdMapper.selectByPrimaryKey(transactionId);
//        if (recd == null) {
//            throw new SacException("交易记录不存在,交易失败");
//        }
//        BigDecimal actualAmount = recd.getActualAmount();//扣除手续费的实际交易金额
//        //package Param
//        String timestamp = String.valueOf(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
//        MultiValueMap<String, Object> requestEntity = new LinkedMultiValueMap<>();
//        jsonObj.put("terminal", TerminalTypeEnum.PC.getCode().toString());
//        jsonObj.put("app_version", APP_VERSION);
//        jsonObj.put("notify_url", "");//TODO notify_url
//        jsonObj.put("company_id", "");//TODO company_id
//        jsonObj.put("player_id", unique.nextId().toString());//TODO company_id
//        jsonObj.put("company_order_id", transactionId.toString());
//        jsonObj.put("amount_money", actualAmount.toPlainString());
//        jsonObj.put("channel_code", ChannelCodeType.alipay.getCode());
//        jsonObj.put("timestamp", timestamp);
//        jsonObj.put("name", recd.getUserRealName());
//        jsonObj.put("card_no", recd.getTargetAddr());
//        jsonObj.put("bank_addr", recd.getBankAddr());
//        jsonObj.put("sign", FBDSignUtil.getSign(requestEntity.toSingleValueMap(), appSecret));
//        rlog.info("charge request url:{}", withdrawUrl);
//        rlog.info("charge request:" + JSON.toJSONString(requestEntity.toSingleValueMap(), false));
//        try {
//            //发起交易
//            ResponseEntity<RemoteWithdrawResult> withdrawResponse = restTemplate.postForEntity(withdrawUrl, requestEntity, RemoteWithdrawResult.class);
//            rlog.info("withdraw response:{}", JSON.toJSONString(withdrawResponse, false));
//            RemoteWithdrawResult body = withdrawResponse.getBody();
//            Integer code = body.getCode();
//            if (code == null || !code.equals(200)) {
//                //交易失败
//                throw new SacException("交易失败");
//            }
//            return body;
//        } catch (Exception e) {
//            rlog.error("charge fail,get exception:{}", e);
//            throw new SacException("充值发生错误,请稍后重试");
//        }
        return null;
    }


    /**
     * 远程交易
     * <p>
     * 1.根据交易记录ID查询交易记录
     * 交易记录不存在->提示交易失败,交易记录不存在
     * 存在->2
     * 2.发起远程交易
     * 3.解析结果
     * </p>
     *
     * @param transactionId 交易记录ID
     * @return 交易结果
     */
    @Override
    public RemoteChargeResult charge(Long transactionId) {
        rlog.debug("alipay charge start:{} ...........", transactionId);
        UserTransactionRecd recd = userTransactionRecdMapper.selectByPrimaryKey(transactionId);
        if (recd == null) {
            throw new SacException("交易记录不存在,交易失败");
        }


        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        BigDecimal actualAmount = recd.getActualAmount();//扣除手续费的实际交易金额

        //package Param
        String timestamp = String.valueOf(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("terminal", TerminalTypeEnum.PC.getCode().toString());
        jsonObj.put("api_version", APP_VERSION);
        jsonObj.put("notify_url", notifyUrl);
        jsonObj.put("company_id", appId);
        jsonObj.put("player_id", unique.nextId().toString());
        jsonObj.put("company_order_id", transactionId.toString());
        jsonObj.put("amount_money", actualAmount.toPlainString());
        jsonObj.put("channel_code", ChannelCodeType.alipay.getCode());
        jsonObj.put("timestamp", timestamp);
        jsonObj.put("sign", FBDSignUtil.getSign(jsonObj.getInnerMap(), appSecret));
        HttpEntity<String> formEntity = new HttpEntity<String>(jsonObj.toString(), headers);

        rlog.info("charge request url:{}", chargeUrl);
        rlog.info("charge request:" + jsonObj.toJSONString(), false);
        try {
            //发起交易
            ResponseEntity<RemoteChargeResult> chargeResponse = restTemplate.postForEntity(chargeUrl, formEntity, RemoteChargeResult.class);
            rlog.info("charge response:{}", JSON.toJSONString(chargeResponse, false));
            RemoteChargeResult body = chargeResponse.getBody();
            Integer code = body.getCode();
            if (code == null || !code.equals(200)) {
                //交易失败
                throw new SacException("交易失败");
            }
            return body;
        } catch (Exception e) {
            rlog.error("charge fail,get exception", e);
            throw new SacException("充值发生错误,请稍后重试");
        }
    }

}
