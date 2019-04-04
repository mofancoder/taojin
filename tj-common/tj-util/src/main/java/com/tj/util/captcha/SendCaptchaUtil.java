package com.tj.util.captcha;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.sun.mail.util.MailSSLSocketFactory;
import com.tj.util.A.SpringContextAware;
import com.tj.util.constant.ConstantConfig;
import com.tj.util.enums.CaptchaTypeEnum;
import com.tj.util.enums.UtilConstants;
import com.tj.util.properties.PropertiesUtil;
import com.tj.util.redis.CloudRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.tj.util.constant.ConstantConfig.*;

@Slf4j
public class SendCaptchaUtil {
    public static IAcsClient acsClient;
    private static ExecutorService executorService = Executors.newFixedThreadPool(8);
    private static JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

    static {
        javaMailSender.setUsername(PropertiesUtil.getStringValue("email.from"));
        javaMailSender.setPassword(PropertiesUtil.getStringValue("email.from.password"));
        javaMailSender.setHost(PropertiesUtil.getStringValue("email.host"));
        javaMailSender.setPort(PropertiesUtil.getIntValue("email.port"));
        javaMailSender.setDefaultEncoding("UTF-8");
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", AliyunSmsAccessKeyId, AliyunSmsAccessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", AliyunSmsProduct, AliyunSmsDomain);
            acsClient = new DefaultAcsClient(profile);
        } catch (ClientException e1) {
            e1.printStackTrace();
        }

        Properties props = new Properties();
        props.setProperty("mail.smtp.host", PropertiesUtil.getStringValue("email.host"));
        props.setProperty("mail.smtp.auth", PropertiesUtil.getStringValue("email.smtp.auth"));
        // props.setProperty("mail.smtp.starttls.enable","true");
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();

        } catch (GeneralSecurityException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        sf.setTrustAllHosts(true);

        //	        props.put("mail.smtp.ssl.socketFactory", sf); //这句加上后才发送成功，不加的话就发送不成功
        //	        props.put("mail.smtp.socketFactory.fallback", "false");
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(PropertiesUtil.getStringValue("email.from"), PropertiesUtil.getStringValue("email.from.password"));
            }
        };
        javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, auth);
        javaMailSender.setSession(session);
    }

    private static JavaMailSender getSender() {
        return javaMailSender;
    }

    /**
     * @param toEmail 接收邮箱，可多个
     */
    public static int sendMail(String captcha, String type, String toEmail, String customer) {
        String key = getCaptchaKey(toEmail, CaptchaTypeEnum.valueOf(type));
        if (captcha == null || type == null || toEmail == null || key == null) {
            return 0;
        }

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(PropertiesUtil.getStringValue("email.from"));
        msg.setTo(toEmail);
        msg.setSubject("安币网");
        CloudRedisService CloudRedisService = SpringContextAware.getBean(CloudRedisService.class);
        if (type.equals(CaptchaTypeEnum.ACCOUNTCHANGE.name())
                || type.equals(CaptchaTypeEnum.FUNDPWDCHANGE.name())
                || type.equals(CaptchaTypeEnum.LOGINPWDCHANGE.name())
                || type.equals(CaptchaTypeEnum.PAY.name())
        ) {
            if (customer != null) {
                long timeout = ConstantConfig.captchaPayTimeout;
                String content = "您的验证码:" + captcha + "  " + timeout / 60 + "分钟有效";
                msg.setText(content);
                try {
                    getSender().send(msg);
                    CloudRedisService.save(key, captcha);
                    CloudRedisService.expire(key, timeout);
                } catch (Exception e) {

                }

                return 1;
            } else return 0;
        } else {
            long timeout = ConstantConfig.captchaTimeout;
            CloudRedisService.save(key, captcha);
            CloudRedisService.expire(key, timeout);
            String content = "您的验证码:" + captcha + "  " + timeout / 60 + "分钟有效";
            msg.setText(content);
            getSender().send(msg);
            return 1;
        }

    }

    public static void sendEmailAsync(String email, String msg) {
        class CallSendEmail implements Callable<Void> {
            String _email;
            String _msg;

            public CallSendEmail(String email, String msg) {
                _email = email;
                _msg = msg;
            }

            @Override
            public Void call() throws Exception {
                SendCaptchaUtil.sendEmail(_email, _msg);
                return null;
            }
        }

        Callable<Void> callSendEmail = new CallSendEmail(email, msg);
        Future<Void> futureCallSendEmail = executorService.submit(callSendEmail);

    }

    public static void sendEmail(String email, String msg) {
        try {

            if (email == null || !email.contains("@")) {
                return;
            }
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(PropertiesUtil.getStringValue("email.from"));
            message.setTo(email);
            message.setSubject("安币网");
            message.setText(msg);
            getSender().send(message);

        } catch (Exception e) {
            log.error("Exception", e);
        }
    }

    public static String getCaptchaKey(String toAddress, CaptchaTypeEnum captchaTypeEnum) {
        String type = captchaTypeEnum.name();
        String key = "Captcha:" + type + ":" + toAddress;
        return key;
    }

    public static String getCaptchaKey(String toAddress, CaptchaTypeEnum captchaTypeEnum, Integer userId) {
        if (null == userId) {
            return null;
        }
        String type = captchaTypeEnum.name();
        String key = "Captcha:" + type + ":" + userId + ":" + toAddress;
        return key;
    }

    public static String getCaptchaKey(String toAddress, String type, String customer) {
        CaptchaTypeEnum captchaTypeEnum = UtilConstants.getEnumFromName(CaptchaTypeEnum.class, type);
        if (null != captchaTypeEnum) {
            if (captchaTypeEnum.equals(CaptchaTypeEnum.ACCOUNTCHANGE)
                    || captchaTypeEnum.equals(CaptchaTypeEnum.FUNDPWDCHANGE)
                    || captchaTypeEnum.equals(CaptchaTypeEnum.LOGINPWDCHANGE)
                    || captchaTypeEnum.equals(CaptchaTypeEnum.PAY)
            ) {
                if (customer != null) {

                    if (toAddress == null || toAddress.equals("")) return null;
                    String key = "Captcha:" + type + ":" + customer + ":" + toAddress;
                    return key;
                } else return null;
            }
            String key = "Captcha:" + type + ":" + toAddress;
            return key;
        } else return null;


    }

    /**
     * 返回1表示发送成功，其他表示发送失败
     *
     * @return
     */
    public static int sendCaptchaSms(String phone, String captcha, String type, String customer) {
        CloudRedisService CloudRedisService = SpringContextAware.getBean(CloudRedisService.class);
        String key = getCaptchaKey(phone, CaptchaTypeEnum.valueOf(type));
        try {
            if (captcha == null || type == null || phone == null || key == null) {
                return 0;
            }

            //可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            //初始化acsClient,暂不支持region化


            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            //必填:待发送手机号
            request.setPhoneNumbers(phone);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(ConstantConfig.ownerConf.getWebSiteName());//"阿尔法"
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(ConstantConfig.ownerConf.getCaptchaTemplateName());
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            request.setTemplateParam("{\"name\":\"" + captcha + "\"}");
            long timeout = 0;

            if (type.equals(CaptchaTypeEnum.ACCOUNTCHANGE.name())
                    || type.equals(CaptchaTypeEnum.FUNDPWDCHANGE.name())
                    || type.equals(CaptchaTypeEnum.LOGINPWDCHANGE.name())
                    || type.equals(CaptchaTypeEnum.PAY.name())) {
                if (customer != null) {
                    timeout = PropertiesUtil.getLongValue("captcha.pay.timeout");
                    CloudRedisService.save(key, captcha);
                    CloudRedisService.expire(key, timeout);
                } else {
                    return 0;
                }
            } else {
                timeout = ConstantConfig.captchaTimeout;
                CloudRedisService.save(key, captcha);
                CloudRedisService.expire(key, timeout);
            }
            //hint 此处可能会抛出异常，注意catch
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            if (sendSmsResponse.getCode().equals("OK")) {
                return 1;
            } else {
                int m = 0;
                if (phone.contains("+") && !phone.contains("+86")
                        && !phone.contains("+886")
                        && !phone.contains("+852")
                        && !phone.contains("+853")
                ) {
                    m = YunPianSendMsg.sendEnglishCaptcha(captcha, phone, (int) timeout / 60);
                } else {
                    m = YunPianSendMsg.sendCaptcha(captcha, phone, (int) timeout / 60);
                }


                if (m != 0) {
                    CloudRedisService.delete(key);
                    return -2;
                } else {
                    return 1;
                }


            }
        } catch (Exception e) {
            CloudRedisService.delete(key);
            log.error("Exception", e);
            return -1;
        }

    }

    public static void sendPhoneMsg(String msg, String phone) {
        try {

            //可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            //初始化acsClient,暂不支持region化

            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", AliyunSmsProduct, AliyunSmsDomain);

            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            //必填:待发送手机号
            request.setPhoneNumbers(phone);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(ConstantConfig.ownerConf.getWebSiteName());
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(ConstantConfig.ownerConf.getCaptchaTemplateName());
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            request.setTemplateParam("{\"name\":\"" + msg + "\"}");
            //hint 此处可能会抛出异常，注意catch
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            System.out.println(sendSmsResponse.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * @param key
     * @param code
     * @param captchaTypeEnum
     * @return 返回 -2 表示需要用户登录
     */
    public static int validateCaptcha(String key, String code, CaptchaTypeEnum captchaTypeEnum, String customer) {

        if (key == null) return -1;
        CloudRedisService CloudRedisService = SpringContextAware.getBean(CloudRedisService.class);
        int defaultErrorCount = 0;
        long outtime = 0;
        if (captchaTypeEnum.equals(CaptchaTypeEnum.ACCOUNTCHANGE)
                || captchaTypeEnum.equals(CaptchaTypeEnum.FUNDPWDCHANGE)
                || captchaTypeEnum.equals(CaptchaTypeEnum.LOGINPWDCHANGE)
                || captchaTypeEnum.equals(CaptchaTypeEnum.PAY)
        ) {
            if (customer == null) {
                return -4;
            }
            outtime = PropertiesUtil.getLongValue("captcha.pay.timeout");
            defaultErrorCount = PropertiesUtil.getIntValue("captcha.pay.errorcount");
        } else {
            outtime = ConstantConfig.captchaTimeout;
            defaultErrorCount = PropertiesUtil.getIntValue("captcha.errorcount");
        }
        String captcha = CloudRedisService.select(key, String.class);
        if (captcha == null || captcha.equals("")) return -2;
        String errorKey = key + captcha;
        if (code.equals(captcha)) {
            CloudRedisService.delete(key);
            CloudRedisService.delete(errorKey);
            return 1;
        } else {
            Object errorobj = CloudRedisService.select(errorKey, Object.class);
            Integer errorCount = 0;
            if (errorobj != null) errorCount = Integer.parseInt(errorobj.toString());
            if (errorCount == null || errorCount == 0) {
                CloudRedisService.save(errorKey, 1);
                CloudRedisService.expire(errorKey, outtime);
                return 0;
            } else {
                errorCount += 1;

                if (errorCount >= defaultErrorCount) {
                    CloudRedisService.delete(key);
                    CloudRedisService.delete(errorKey);
                    return -3;
                } else {
                    CloudRedisService.update(errorKey, errorCount);
                    return 0;
                }
            }
        }
    }

    /**
     * 验证码统一验证接口
     *
     * @param toAddress
     * @param type
     * @param code
     * @return 1表示成功
     */
    public static int validateCaptcha(String toAddress, String type, String code) {
        CaptchaTypeEnum captchaTypeEnum = UtilConstants.getEnumFromName(CaptchaTypeEnum.class, type);
        String key = SendCaptchaUtil.getCaptchaKey(toAddress, CaptchaTypeEnum.valueOf(type));
        return validateCaptcha(key, code, captchaTypeEnum.name());
    }

    /**
     * 删除缓存在redis 中key值
     *
     * @param toAddress
     * @param captchaTypeEnum
     */
    public static void deleteKey(String toAddress, CaptchaTypeEnum captchaTypeEnum) {
        CloudRedisService CloudRedisService = SpringContextAware.getBean(CloudRedisService.class);
        String key = SendCaptchaUtil.getCaptchaKey(toAddress, captchaTypeEnum);
        if (key != null) {
            CloudRedisService.delete(key);
        }

    }

    public static void main(String[] args) {
        //System.out.println(sendCaptchaSms("18188621491","1235",CaptchaTypeEnum.LOGIN.name()));;
		/*try {
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setFrom(PropertiesUtil.getStringValue("email.from"));
			msg.setTo("870883643@qq.com");
			msg.setSubject("安币网");

			String content =  "您的验证码888   "+1000l/60+"分钟有效";
			msg.setText(content);
			getSender().send(msg);

		} catch (Exception e) {
			e.printStackTrace();
		}
		//sendMail("寄给你一百万了", CaptchaTypeEnum.EMAILFIND.name(), );
		String type="EMAILFIND";
		if(type.equals(CaptchaTypeEnum.PWDFIND.name())) {
			System.out.println("ddd");
		}*/
        //sendEmail("1364387586@qq.com", "测试发送");
        System.out.println(ConstantConfig.captchaTimeout / 60);
        //sendPhoneMsg("123", "13557832237");
    }
}
