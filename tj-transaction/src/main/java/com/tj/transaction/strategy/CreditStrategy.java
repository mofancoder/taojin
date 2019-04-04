//package com.tj.transaction.strategy;
//
//import com.tj.dto.RemoteChargeResult;
//import com.tj.dto.RemoteWithdrawResult;
//import com.tj.transaction.dao.UserTransactionRecdMapper;
//import com.tj.util.enums.PlatformTypeEnum;
//import com.tj.util.log.Rlog;
//import com.tj.util.unique.Unique;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import javax.annotation.Resource;
//
///**
// * @program: tj-core
// * @description: 银行卡支付策略
// * @author: liang.song
// * @create: 2018-11-28-11:51
// **/
//@Component
//public class CreditStrategy extends AbstractTransactionStrategy {
//    private final Rlog rlog;
//    private final RestTemplate restTemplate;
//    private final Unique unique;
//    @Value("${transaction.app-id}")
//    private String appId;
//    @Value("${transaction.app-secret}")
//    private String appSecret;
//    @Value("${transaction.credit-charge-url}")
//    private String chargeUrl;
//    @Resource
//    private UserTransactionRecdMapper userTransactionRecdMapper;
//
//    @Autowired
//    public CreditStrategy(Unique unique, Rlog rlog, RestTemplate restTemplate) {
//        this.rlog = rlog;
//        this.restTemplate = restTemplate;
//        this.unique = unique;
//    }
//
//    @Override
//    public PlatformTypeEnum type() {
//        return PlatformTypeEnum.credit;
//    }
//
//    @Override
//    public RemoteChargeResult charge(Long transactionId) {
//        return RemoteChargeResult.builder()
//                .transactionId(transactionId.toString())
//                .thirdPartyId(unique.nextId().toString())
//                .payUrl("www.test.com")
//                .build();
//    }
//
//    @Override
//    public RemoteWithdrawResult withdraw(Long transactionId) {
//        return RemoteWithdrawResult.builder()
//                .transactionId(transactionId.toString())
//                .thirdPartyId(unique.nextId().toString())
//                .payUrl("www.test.com")
//                .build();
//    }
//    /**
//     * 远程交易
//     * <p>
//     *     1.根据交易记录ID查询交易记录
//     *      交易记录不存在->提示交易失败,交易记录不存在
//     *      存在->2
//     *     2.发起远程交易
//     *     3.解析结果
//     * </p>
//     * @param transactionId 交易记录ID
//     * @return 交易结果
//     */
////    @Override
////    public RemoteChargeResult charge(Long transactionId) {
////        //TODO mock 交易结果返回
////        rlog.debug("alipay charge start:{} ...........",transactionId);
////        UserTransactionRecd recd = userTransactionRecdMapper.selectByPrimaryKey(transactionId);
////        if(recd==null){
////            throw new SacException("交易记录不存在,交易失败");
////        }
////        BigDecimal actualAmount = recd.getActualAmount();//扣除手续费的实际交易金额
////        //package Param
////        String timestamp = String.valueOf(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000);
////        MultiValueMap<String, String> requestEntity = new LinkedMultiValueMap<>();
////        requestEntity.add("transactionId", transactionId.toString());
////        requestEntity.add("amount", actualAmount.toPlainString());
////        requestEntity.add("userRemark", recd.getUserRemark());
////        requestEntity.add("appid", appId);
////        requestEntity.add("timestamp", timestamp);
////        requestEntity.add("_sign", FBDSignUtil.sign(requestEntity.toSingleValueMap(), appSecret));
////        rlog.info("charge request:"+ JSON.toJSONString(requestEntity.toSingleValueMap(),false));
////        try{
////            //发起交易
////            ResponseEntity<String> chargeResponse = restTemplate.postForEntity(chargeUrl, requestEntity, String.class);
////            rlog.info("charge response:{}",JSON.toJSONString(chargeResponse,false));
////            boolean xxSuccessful=chargeResponse.getStatusCode().is2xxSuccessful();
////            if(!xxSuccessful){
////                rlog.error("charge fail ,http code is not 2xx,is:{}",chargeResponse.getStatusCode().value());
////                throw new SacException("充值发生错误,请稍后重试");
////            }
////            String body = chargeResponse.getBody();
////            if(body==null){
////                rlog.error("charge fail,response body is null");
////                throw new SacException("充值发生错误,请稍后重试");
////            }
////            JSONObject jsonObject = JSON.parseObject(body);
////            String thirdPartyId = jsonObject.getString("thirdPartyId");
////            if(StringUtils.isEmpty(thirdPartyId)){
////                rlog.error("charge fail,thirdPartyId is empty");
////                throw new SacException("充值发生错误,请稍后重试");
////            }
////            String url = jsonObject.getString("url");
////            if(StringUtils.isEmpty(url)){
////                rlog.error("charge fail, payUrl is empty");
////                throw new SacException("充值发生错误,请稍后重试");
////            }
////
////            return RemoteChargeResult.builder()
////                    .payUrl(url)
////                    .thirdPartyId(thirdPartyId)
////                    .transactionId(transactionId.toString())
////                    .build();
////        }catch (Exception e){
////            rlog.error("charge fail,get exception:{}",e);
////            throw new SacException("充值发生错误,请稍后重试");
////        }
////    }
//}
