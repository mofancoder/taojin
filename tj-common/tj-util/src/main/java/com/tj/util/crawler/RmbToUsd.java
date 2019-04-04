package com.tj.util.crawler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tj.util.enums.CoinIdEnum;
import com.tj.util.enums.CoinRateTypeEnum;
import com.tj.util.enums.RateTypeEnum;
import com.tj.util.enums.UtilConstants;
import com.tj.util.http.Https;
import com.tj.util.redis.CloudRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class RmbToUsd {
    final static BigDecimal DefaultRate = new BigDecimal("6.3365");
    final static String RemoteUrl = "http://op.juhe.cn/onebox/exchange/query?key=7864be51957a2b168f11305e7945c544";
    final static String CoinToUsdUrl = "https://api.coinmarketcap.com/v1/ticker/";
    //现在百度爬取的汇率
    final static String RemoteUrl2 = "https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?query=1";
    private final static long scheduleTime = 10; //单位秒
    private static final Map<String, Object> coinMap;
    //fcoin交易所 例如https://api.fcoin.com/v2/market/ticker/ethusdt
    static String FcoinRetaCoinUrl = "https://api.fcoin.com/v2/market/ticker/";
    static Map DefaultRateMap;
    //来源于chaoex交易所
    static String SacToEthUrl = "https://www.chaoex.io/12lian/quote/realTime?baseCurrencyId=3&tradeCurrencyId=74";
    static String SacToChexUrl = "https://www.chaoex.io/12lian/quote/realTime?baseCurrencyId=70&tradeCurrencyId=74";
    static String ChexToEthtUrl = "https://www.chaoex.io/12lian/quote/realTime?baseCurrencyId=3&tradeCurrencyId=70";
    static String FgcToChexUrl = "https://www.chaoex.io/12lian/quote/realTime?baseCurrencyId=70&tradeCurrencyId=72";

    //测试地址
//    final static String SacToBtcUrl = "http://www.tradding-dev.hengxingstar.com/market_center/api/MarketApi/get_hang_qing_list.do";
    static {
        DefaultRateMap = new HashMap();
        DefaultRateMap.put("RMB", "6.9");
        DefaultRateMap.put("USD", "1");
        DefaultRateMap.put("EUR", "0.8514");
        DefaultRateMap.put("GBP", "0.7524");
        DefaultRateMap.put("JPY", "110.45");
        DefaultRateMap.put("HKD", "7.8479");


    }

    static {
        coinMap = new HashMap<>();
        coinMap.put("usd", 0);
        coinMap.put("btc", 0);
        coinMap.put("rmb", 0);
        coinMap.put("eur", 0);
        coinMap.put("gbp", 0);
        coinMap.put("jpy", 0);
        coinMap.put("hkd", 0);
    }

    @Autowired
    CloudRedisService redisService;
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private String fcoin_sac_eth_url = "https://api.fcoin.com/v2/market/trades/saceth";

    private static String coinIdEnum2CoinType(CoinIdEnum coinIdEnum) {
        if (coinIdEnum == null) return "";
        /**
         *    bitcoin,
         ethereum,
         */
        if (coinIdEnum.equals(CoinIdEnum.BTC)) {
            return CoinRateTypeEnum.bitcoin.name();
        }
        if (coinIdEnum.equals(CoinIdEnum.ETH)) {
            return CoinRateTypeEnum.ethereum.name();
        }
        if (coinIdEnum.equals(CoinIdEnum.USDT)) {
            return CoinRateTypeEnum.tether.name();
        }
        if (coinIdEnum.equals(CoinIdEnum.SAC)) {
            return coinIdEnum.name();
        }
        return coinIdEnum.name();
    }

    public static void main(String[] args) {
        RmbToUsd n = new RmbToUsd();
        CoinPriceInfo coinPriceInfo = n.getCoinPriceInfo(CoinIdEnum.SAC);
        System.out.println(coinPriceInfo);
    }

    /*-------------------------------------------------------------------
    {
        "reason": "查询成功",
        "result": {
            "update": "2018-03-01 10:19:13",
            "list": [
             货币名称，交易单位，现汇买入价，现钞买入价，现钞卖出价，中行折算价
			["美元",    "100", "632.5100", "627.3100", "635.2300", "633.6500"],
			["日元",    "100", "5.9193",   "5.7321",   "5.9669",   "5.9431"],
			["欧元",    "100", "769.3300", "745.0000", "775.5100", "772.4200"],
			["英镑",    "100", "867.6000", "840.1600", "874.5600", "871.0800"],
			["澳元",    "100", "487.4100", "472.0000", "491.3300", "489.3700"],
			["加拿大元","100", "491.4500", "475.9000", "495.3900", "493.4200"],
			["瑞士法郎","100", "667.4900", "646.3800", "672.8500", "670.1700"],
			["港币",    "100", "80.7900",  "80.2200",  "81.1100",  "80.9500"],
			["新西兰元","100", "453.8400", "439.4800", "457.4800", "455.6600"],
			["新加坡元","100", "475.8100", "460.7600", "479.6300", "477.7200"]
		 ] },
        "error_code": 0
    }
    -------------------------------------------------------------------*/
    public BigDecimal getRateFromRemote(String rateName) {
        String url = RemoteUrl2;
        try {
            for (RateTypeEnum rateTypeEnum : RateTypeEnum.values()) {
                if (rateTypeEnum.name().equals(rateName)) {
                    url = url + URLEncoder.encode("美元等于多少") + URLEncoder.encode(rateTypeEnum.getRateName()) + "&resource_id=6017";
                }
            }
            String result = Https.get(url);
            JSONObject rateListJson = JSONObject.parseObject(result);
            if (null == rateListJson) {
                return new BigDecimal(DefaultRateMap.get(rateName).toString());
            }

            String status = rateListJson.getString("status");
            if (!status.equals("0")) {
                return new BigDecimal(DefaultRateMap.get(rateName).toString());
            }

            JSONArray resultJson = rateListJson.getJSONArray("data");
            JSONObject data = resultJson.getJSONObject(0);
            String number2 = data.get("number2").toString();

            return new BigDecimal(number2);
        } catch (Exception e) {
            return new BigDecimal(DefaultRateMap.get(rateName).toString());
        }
    }

    public Map<String, BigDecimal> getRateMap(String rateName) {

        final String Key = "rate:" + rateName + "2usd";
        final Long timeOut = 60 * 30L;
        BigDecimal price = new BigDecimal(DefaultRateMap.get(rateName).toString());

        BigDecimal ratInRedis = redisService.select(Key, BigDecimal.class);
        if (null == ratInRedis) {
            BigDecimal rateRemote = getRateFromRemote(rateName);
            if (null != rateRemote) {
                redisService.save(Key, rateRemote);
                redisService.expire(Key, timeOut);
                price = rateRemote;
            }
        } else {
            price = ratInRedis;
        }

        Map<String, BigDecimal> priceMap = com.google.common.collect.Maps.newHashMap();
        BigDecimal high = price.multiply(new BigDecimal("1.2"));
        BigDecimal low = price.multiply(new BigDecimal("0.8"));

        priceMap.put("price", price);
        priceMap.put("high", high);
        priceMap.put("low", low);

        return priceMap;
    }

    private Map<String, Object> saveCoinToUsd(String url, String key, String coinType) {
        Map<String, Object> map = new HashMap();
        try {

            String result = Https.get(url);
            if (result.trim().isEmpty()) {
                return null;
            }
            BigDecimal rateRmb = new BigDecimal(getRateMap(RateTypeEnum.RMB.name()).get("price").toString());
            BigDecimal priceUsd = BigDecimal.valueOf(0);
            BigDecimal priceBtc = BigDecimal.valueOf(0);
            BigDecimal priceRmb = BigDecimal.valueOf(0);
            if (coinType.equals(CoinIdEnum.CHEX.name()) || coinType.equals(CoinIdEnum.FGC.name())) {

                //来源chaoex交易所
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (null == jsonObject || !jsonObject.getString("status").equals("200")) {
                    return null;
                }
                JSONObject attachment = jsonObject.getJSONObject("attachment");
                if (null == attachment) {
                    return null;
                }
                if (coinType.equals(CoinIdEnum.FGC.name())) {
                    BigDecimal priceChex = attachment.getBigDecimal("last");
                    CoinPriceInfo chexMap = getCoinPriceInfo(CoinIdEnum.CHEX);
                    BigDecimal chexPriceUsd = chexMap.priceUsd;
                    priceBtc = chexMap.priceBtc.multiply(priceChex);
                    priceUsd = priceChex.multiply(chexPriceUsd);
                    priceRmb = priceUsd.multiply(rateRmb);

                } else {
                    BigDecimal priceEth = attachment.getBigDecimal("last");
                    CoinPriceInfo ethMap = getCoinPriceInfo(CoinRateTypeEnum.ethereum.toString());
                    BigDecimal ethPriceUsd = ethMap.priceUsd;
                    priceBtc = ethMap.priceBtc.multiply(priceEth);
                    priceUsd = priceEth.multiply(ethPriceUsd);
                    priceRmb = priceUsd.multiply(rateRmb);
                }


            } else if (coinType.equals(CoinIdEnum.SAC.name())) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                //来源于Fcoin
                String state = jsonObject.getString("status");
                if ("0".equals(state)) {
                    JSONArray array = jsonObject.getJSONArray("data");
                    if (array != null && array.size() > 0) {
                        JSONObject json = (JSONObject) array.get(0);
                        String pricestr = json.getString("price");
                        BigDecimal priceEth = new BigDecimal(pricestr);
                        CoinPriceInfo ethMap = getCoinPriceInfo(CoinRateTypeEnum.ethereum.toString());
                        BigDecimal ethPriceUsd = ethMap.priceUsd;
                        priceBtc = ethMap.priceBtc.multiply(priceEth);
                        priceUsd = priceEth.multiply(ethPriceUsd);
                        priceRmb = priceUsd.multiply(rateRmb);

                    }
                }

            } else {
                //非sac
                JSONArray jsonArray = JSONArray.parseArray(result);
                if (null == jsonArray) {
                    return null;
                }
                JSONObject rateJson = jsonArray.getJSONObject(0);
                priceUsd = rateJson.getBigDecimal("price_usd");
                priceBtc = rateJson.getBigDecimal("price_btc");
                priceRmb = priceUsd.multiply(rateRmb);
            }
            map.put("timestamp", new Date().getTime());
            map.put("priceUsd", priceUsd);
            map.put("priceBtc", priceBtc);
            map.put("priceRmb", priceRmb);

            BigDecimal priceEur = getRateMap(RateTypeEnum.EUR.name()).get("price");
            BigDecimal priceGbp = getRateMap(RateTypeEnum.GBP.name()).get("price");
            BigDecimal priceJpy = getRateMap(RateTypeEnum.JPY.name()).get("price");
            BigDecimal priceHkd = getRateMap(RateTypeEnum.HKD.name()).get("price");

            priceEur = priceEur.multiply(priceUsd);
            priceGbp = priceGbp.multiply(priceUsd);
            priceJpy = priceJpy.multiply(priceUsd);
            priceHkd = priceHkd.multiply(priceUsd);

            map.put("priceEur", priceEur);
            map.put("priceGbp", priceGbp);
            map.put("priceJpy", priceJpy);
            map.put("priceHkd", priceHkd);

            if (null != priceUsd && priceUsd.compareTo(BigDecimal.ZERO) > 0
                    && null != priceBtc && priceBtc.compareTo(BigDecimal.ZERO) > 0
                    && null != priceRmb && priceRmb.compareTo(BigDecimal.ZERO) > 0) {
                redisService.save(key, map);
            } else {
                log.error("RmbToUsd.saveCoinToUsd fail| 获取汇率转换有误");
            }

        } catch (Exception e) {
            log.error("RmbToUsd.saveCoinToUsd fail|", e);
        }
        return map;
    }

    public Map<String, Object> getCoinToUsdByCoinName(String coinName) {
        CoinIdEnum coinIdEnum = UtilConstants.getEnumFromName(CoinIdEnum.class, coinName);
        if (null == coinIdEnum) {
            return coinMap;
        }
        String coinType = coinIdEnum2CoinType(coinIdEnum);
        return getPriceMap(coinType);
    }

    public CoinPriceInfo getCoinPriceInfoByCoinName(String coinName) {
        CoinIdEnum coinIdEnum = UtilConstants.getEnumFromName(CoinIdEnum.class, coinName);
        if (null == coinIdEnum) {
            return new CoinPriceInfo();
        }
        String coinType = coinIdEnum2CoinType(coinIdEnum);
        return getCoinPriceInfo(coinType);
    }

    public CoinPriceInfo getCoinPriceInfo(CoinIdEnum coinIdEnum) {
        if (null == coinIdEnum) return new CoinPriceInfo();
        String coinType = coinIdEnum2CoinType(coinIdEnum);
        return getCoinPriceInfo(coinType);
    }

    /**
     * @param coinType
     * @return
     */
    private CoinPriceInfo getCoinPriceInfo(String coinType) {
        final String Key = "rate:" + coinType + "2usd";
        final String url;
        if (coinType.equals(CoinIdEnum.SAC.name())) {
//            url = SacToBtcUrl;
            url = fcoin_sac_eth_url;
        } else if (coinType.equals(CoinIdEnum.CHEX.name())) {
            url = ChexToEthtUrl;
        } else if (coinType.equals(CoinIdEnum.FGC.name())) {
            url = FgcToChexUrl;
        } else {
            url = CoinToUsdUrl + coinType;
        }

        Map<String, Object> coinToUsdMap = redisService.select(Key, Map.class);
        if (null == coinToUsdMap) {
            coinToUsdMap = saveCoinToUsd(url, Key, coinType);
        } else {
            Date now = new Date();
            Long updateTime = (Long) coinToUsdMap.get("timestamp");
            if (((Long) now.getTime() - updateTime) / 1000 > scheduleTime) {
                cachedThreadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        saveCoinToUsd(url, Key, coinType);
                    }
                });
            }
        }
        CoinPriceInfo coinPriceInfo = new CoinPriceInfo();
        coinPriceInfo.priceBtc = (BigDecimal) coinToUsdMap.get("priceBtc");
        coinPriceInfo.priceRmb = (BigDecimal) coinToUsdMap.get("priceRmb");
        coinPriceInfo.priceUsd = (BigDecimal) coinToUsdMap.get("priceUsd");
        coinPriceInfo.priceEur = (BigDecimal) coinToUsdMap.get("priceEur");
        coinPriceInfo.priceGbp = (BigDecimal) coinToUsdMap.get("priceGbp");
        coinPriceInfo.priceJpy = (BigDecimal) coinToUsdMap.get("priceJpy");
        coinPriceInfo.priceHkd = (BigDecimal) coinToUsdMap.get("priceHkd");
        return coinPriceInfo;
    }

    /**
     * @param coinType
     * @return
     */
    private Map<String, Object> getPriceMap(String coinType) {
        final String Key = "rate:" + coinType + "2usd";
        final String url;
        if (coinType.equals(CoinIdEnum.SAC.name())) {
//            url = SacToBtcUrl;
            url = SacToEthUrl;
        } else if (coinType.equals(CoinIdEnum.CHEX.name())) {
            url = ChexToEthtUrl;
        } else if (coinType.equals(CoinIdEnum.FGC.name())) {
            url = FgcToChexUrl;
        } else {
            url = CoinToUsdUrl + coinType;
        }

        Map<String, Object> coinToUsdMap = redisService.select(Key, Map.class);
        if (null == coinToUsdMap) {
            coinToUsdMap = saveCoinToUsd(url, Key, coinType);
        } else {
            Date now = new Date();
            Long updateTime = (Long) coinToUsdMap.get("timestamp");
            if (((Long) now.getTime() - updateTime) / (1000 * 60) > scheduleTime) {
                cachedThreadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        saveCoinToUsd(url, Key, coinType);
                    }
                });
            }
        }
        CoinPriceInfo coinPriceInfo = new CoinPriceInfo();
        coinPriceInfo.priceBtc = (BigDecimal) coinToUsdMap.get("priceBtc");
        coinPriceInfo.priceRmb = (BigDecimal) coinToUsdMap.get("priceRmb");
        coinPriceInfo.priceUsd = (BigDecimal) coinToUsdMap.get("priceUsd");
        coinPriceInfo.priceEur = (BigDecimal) coinToUsdMap.get("priceEur");
        coinPriceInfo.priceGbp = (BigDecimal) coinToUsdMap.get("priceGbp");
        coinPriceInfo.priceJpy = (BigDecimal) coinToUsdMap.get("priceJpy");
        coinPriceInfo.priceHkd = (BigDecimal) coinToUsdMap.get("priceHkd");
        return coinToUsdMap;
    }

    /**
     * 获取用户币种数量换算成其他钞票
     *
     * @param coinName
     * @param amount
     * @return
     */
    public Map<String, Object> getCoinToMoneyByCoinType(String coinName, BigDecimal amount) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> priceMap = getCoinToUsdByCoinName(coinName);
        if (null == priceMap || priceMap.size() == 0) {
            return null;
        }
        for (Map.Entry<String, Object> entry : priceMap.entrySet()) {
            BigDecimal amountTo = BigDecimal.ZERO;

            String name = entry.getKey().replaceAll("price", "").toLowerCase();
            if (null == entry.getValue() || "".equals(entry.getValue().toString())) {
                map.put(name, amountTo);
            } else if (entry.getKey().toString().equals("timestamp")) {

            } else {
                amountTo = new BigDecimal(entry.getValue().toString());
                amountTo = amountTo.multiply(amount);
                map.put(name, amountTo);
            }
        }
        //利用反射
//		Class cls = coinPriceInfo.getClass();
//		Field[] fields = cls.getDeclaredFields();
//		for(int i=0; i<fields.length; i++){
//			Field f = fields[i];
//			f.setAccessible(true);
//			BigDecimal amountTo = BigDecimal.ZERO;
//			String name = f.getName().replaceAll("price","").toLowerCase();
//			try {
//				if (null == f.get(coinPriceInfo) || "".equals(f.get(coinPriceInfo))) {
//
//				}else{
//					amountTo = new BigDecimal(f.get(coinPriceInfo).toString());
//				}
//			} catch (IllegalAccessException e) {
//				RLog.error("getCoinToMoneyByCoinType fail|coinName="+coinName,e);
//			}
//			map.put(name,amountTo);
//		}
        return map;
    }

    public static class CoinPriceInfo {
        public BigDecimal priceUsd = BigDecimal.ZERO;//美元
        public BigDecimal priceBtc = BigDecimal.ZERO;//btc
        public BigDecimal priceRmb = BigDecimal.ZERO;//人民币
        public BigDecimal priceEur = BigDecimal.ZERO;//欧元
        public BigDecimal priceGbp = BigDecimal.ZERO;//英镑
        public BigDecimal priceJpy = BigDecimal.ZERO;//日元
        public BigDecimal priceHkd = BigDecimal.ZERO;//港币
    }
}
