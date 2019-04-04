package com.tj.util.aliyun;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigRequest;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RiskManager {

    private static String OldAndroidWalletSildeKey = "FFFFA0000000017B8F8A";
    private static String AndroidWalletSildeKey = "FFFFA0000000017BC0B2";
    private static String IosWalletSildeKey = "FFFFI0000000017BBEAC";
    private static String AndroidTRADESildeKey = "FFFFI0000000017B9106";
    private static String AliyunRiskAccessKeyId = "LTAICD3xZku9eion";
    private static String AliyunRiskAccessKeySecret = "ODyGahpQXrEXw1JkYmqHE6QaOUJAgU";
    private static DefaultAcsClient acsClient;

    static {
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", AliyunRiskAccessKeyId, AliyunRiskAccessKeySecret);

        acsClient = new DefaultAcsClient(profile);
    }

    public static AuthenticateSigResponse checkAndrWalletRisk(String sessionId, String sig, String token, String scene, String ip) throws ClientException {
        return checkRisk(sessionId, sig, token, scene, ip, AndroidWalletSildeKey);
    }

    public static AuthenticateSigResponse checkIOSWalletRisk(String sessionId, String sig, String token, String scene, String ip) throws ClientException {
        return checkRisk(sessionId, sig, token, scene, ip, IosWalletSildeKey);
    }

    public static AuthenticateSigResponse checkTRADERisk(String sessionId, String sig, String token, String scene, String ip) throws ClientException {
        return checkRisk(sessionId, sig, token, scene, ip, AndroidTRADESildeKey);
    }

    private static AuthenticateSigResponse checkRisk(String sessionId, String sig, String token, String scene, String ip, String appKey) {

        try {
            IAcsClient client = null;
			/*	if(!appKey.equals(AndroidTRADESildeKey)){
				client =  acsClient;
			}else{
				 client = SendCaptchaUtil.acsClient;
			}*/

            client = acsClient;
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "afs", "afs.aliyuncs.com");
            //response的code枚举：100验签通过，900验签失败

            AuthenticateSigRequest request = new AuthenticateSigRequest();
            request.setSessionId(sessionId);// 必填参数，从前端获取，不可更改
            request.setSig(sig);// 必填参数，从前端获取，不可更改
            request.setToken(token);// 必填参数，从前端获取，不可更改
            request.setScene(scene);// 必填参数，从前端获取，不可更改
            request.setAppKey(appKey);// 必填参数，后端填写
            request.setRemoteIp(ip);// 必填参数，后端填写
            AuthenticateSigResponse response = client.getAcsResponse(request);
            return response;
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            String wtoken = "trWW_m1fxAEOqFpgOu57xVh6h5zBTkAtNc5Gu1N/Cns29DxpkhDolYCbBWDu/N7vfqiPHabfXNScdRDKOomrjVj9bzd+Fwab1mEV+3iMhe5y6Tosua4207EW/gFtIHzao+A/f4oSZIFP/XDFH9UFixLVfVWQrbpGzFErU1nxenvESaWrotevQZ5JMVWwb7WZR7k5hrjca7AnGfTrllPMrRTlka9V5cBeUd18Iwm2yU1uXYcfTFbu3lw8/RAywG7dYYq4IXhDN31QRSz2mJxI5IGs+Pe+G9z89/OLrgix42jG0TBVPraKEBTM20YJRYviNf6gPFmKR5wT3fOnukoM3rYlt69Z4fhc1+FGYQq8JWATF/W8QJqnw3BinZqKYznMXQJGltalR2yYXFxXh+p3tk6vjVvVlGy83zkV5IkwBHFdUrf2u8H9ZBMlPm0fyCnk6cxPvQmj9PrFm0axXMDChEA2W4w==";
            String sessionId = "nc1-01kpKpD9cZIa0BMLxnFjaumeucxSNUmC6Qdnr3Ad5xBdGh_AArOTW-vIrSqZ9g-mss-nc2-05BycC1RVYdxRGBYboM8HvXEm-Kv1MubSJfq6y5xlnKLrQkZU6vRs6gx3y5NbZ1WCxM4G8vde1f1vtlwUuocqZpyt6phY6nTIsPA5NbiyXE2aHg3Ec9X7wqD9cVwq9W1TnGwoQyjb7x6twtQN1IbD1j5TQEU7OAf0UChDM8l5lMURvhI-xuptx_UZoZ1zSb4eqD5pYjsdPhpU9rhKnuvloqbgDWwxJbuWqxHT9xhbyA_2G-HfR5Bga8zQsv-3ewkXnQNddJjakOoUoZUV6Yn1EhDxA2rXEJDVoBlbK-RrJb_nO80cidayj1_iATRoDe1sF_dlm1h2xQezZfkssiT6YvFsAxQHojeOF39MSL4bxBD6lTNuXWObxhMa4sSKBdxnv9uEZwZ2c6WYYJaX0K_sJd20ySSmWNNSZfHE4hFObg6A-nc3-012fFSZXkPovoERrcGicZEUT4SiOJHe142GNDLkZkxCbrBQfKIidIEFC5kWiXVlWoMwiGLqof7h_BstRfTsDbMOFXfx7xeGRuuDJ6uQ66lZisKWPHyPdk9tH3BbYzZhpBBjTWjYMwcgHXCIho4mIyqdg-nc4-FFFFI0000000017BBEAC";
            AuthenticateSigResponse response = checkIOSWalletRisk(sessionId, "sig", wtoken, "登录", "218.18.77.68");
            System.out.println(response.getCode() + response.getMsg());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
