package com.tj.util.aliyun;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.Callback;
import com.aliyun.oss.model.Callback.CalbackBodyType;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.tj.util.properties.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * 阿里云oss文件上传工具类
 * Created by lh on 2018/5/7.
 */
@Slf4j
public class OssUtil {


    private static String bucketName = PropertiesUtil.getStringValue("aliyun.oss.bucketName");
    private static String endpoint = PropertiesUtil.getStringValue("aliyun.oss.endpoint");
    //外部访问地址
    private static String outEndpoint = PropertiesUtil.getStringValue("aliyun.oss.outEndpoint");
    private static String accessKeyId = PropertiesUtil.getStringValue("aliyun.oss.accessKeyId");
    private static String accessKeySecret = PropertiesUtil.getStringValue("aliyun.oss.accessKeySecret");


    /**
     * 文件上传
     *
     * @param filepath 本地地址
     * @param firstKey 储存文件夹
     * @return
     */
    public static String getOSSUrl(String filepath, String firstKey) {
        try {
            File file = new File(filepath);
            String fileName = filepath.split("/")[filepath.split("/").length - 1];
            String OssUrl = null;
            if (file.exists() && file.length() > 0) {
                OssUrl = OssUtil.getOssFilePath(fileName, filepath, firstKey);
            }
            return OssUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param file
     * @param firstKey
     * @return
     */
    public static String getOSSUrl(File file, String firstKey) {
        try {
            String fileName = file.getName();
            String OssUrl = null;
            InputStream input = new FileInputStream(file);
            OSSClient ossClient = getOSSUtil();
            if (null == ossClient) {
                return null;
            } else {
                ObjectMetadata objectMeta = new ObjectMetadata();
                objectMeta.setContentLength(file.length());

                String key = firstKey + fileName;
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                PutObjectRequest putObj = new PutObjectRequest(bucketName, key, input, objectMeta);
                putObj.setProcess("125");
                PutObjectResult putObjectResult = ossClient.putObject(putObj);
                stopWatch.stop();
                ResponseMessage resp = putObjectResult.getResponse();
                String sMsg = "ossTime(ms):" + stopWatch.getTotalTimeMillis();
                if (resp != null) {
                    sMsg += "|isSuc:" + resp.isSuccessful() + "|error:" + resp.getErrorResponseAsString();
                }

                log.debug("Object:" + key + "|" + sMsg);

                OssUrl = outEndpoint + "/" + key;
            }
            return OssUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getOSSUrlToResponse(File file, String firstKey) {
        try {
            String fileName = file.getName();
            String OssUrl = null;
            InputStream input = new FileInputStream(file);
            OSSClient ossClient = getOSSUtil();
            if (null == ossClient) {
                return null;
            } else {
                ObjectMetadata objectMeta = new ObjectMetadata();
                objectMeta.setContentLength(file.length());

                String key = firstKey + fileName;
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                PutObjectRequest putObj = new PutObjectRequest(bucketName, key, input, objectMeta);
                putObj.setProcess("125");
                Callback callback = new Callback();
                callback.setCallbackUrl("http://47.52.128.11:9005/wallet/app/user/getConnectionInfo.do");
                callback.setCallbackBody("{\\\"mimeType\\\":1235,\\\"size\\\":25}");
                callback.setCalbackBodyType(CalbackBodyType.JSON);
                putObj.setCallback(callback);
                PutObjectResult putObjectResult = ossClient.putObject(putObj);
                stopWatch.stop();
                String sMsg = "ossTime(ms):" + stopWatch.getTotalTimeMillis() + putObjectResult.getResponse().isSuccessful();
                log.debug("Object:" + key + "|" + sMsg);

                OssUrl = outEndpoint + "/" + key;
            }
            return OssUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getOSSUrl(InputStream input, String firstKey, String fileName, long fileLength) {
        try {
            String OssUrl = null;
            OSSClient ossClient = getOSSUtil();
            if (null == ossClient) {
                return null;
            } else {
                ObjectMetadata objectMeta = new ObjectMetadata();
                objectMeta.setContentLength(fileLength);
                String key = firstKey + fileName;

                ossClient.putObject(bucketName, key, input, objectMeta);
                log.debug("Object：" + key + "存入OSS成功。");
                OssUrl = outEndpoint + "/" + key;
            }
            return OssUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getOssFilePath(String fileName, String uploadFilePath, String firstKey) {
        URL uri = null;
        OSSClient client = getOSSUtil();
        //上传图片
        String key = firstKey + fileName; //指定文件上传到bucket下面的那个文件夹下及文件名
        Date expiration = new Date(new Date().getTime() + 3600l * 1000 * 24 * 365 * 10);
        boolean isSuccess = uploadFile(client, bucketName, key, uploadFilePath);//指定bucket
        //设置有效时间
        if (isSuccess) {
            uri = client.generatePresignedUrl(bucketName, key, expiration);
        }
        return uri.toString();
    }

    private static boolean uploadFile(OSSClient client, String bucketName, String key, String filePath) {
        int MAX_TRY = 3;
        int downloadTurn = 0;
        boolean uploadSuccess = false;
        while (downloadTurn < MAX_TRY) {
            try {
                File file = new File(filePath);
                if ((!file.exists()) || file.length() == 0) {
                    uploadSuccess = false;
                    break;
                }
                ObjectMetadata objectMeta = new ObjectMetadata();
                objectMeta.setContentLength(file.length());
                if (!client.doesObjectExist(bucketName, key)) {
                    InputStream input = new FileInputStream(file);
                    client.putObject(bucketName, key, input, objectMeta);
                    System.out.println(filePath + "上传成功!");
                    uploadSuccess = true;
                    break;
                } else {
                    uploadSuccess = true;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return uploadSuccess;
    }

    /**
     * 删除指定文件
     *
     * @param folder 文件名
     * @param key    文件名
     */
    public static void deleteOSSFileUitl(String folder, String key) {
        OSSClient ossClient = getOSSUtil();
        ossClient.deleteObject(bucketName, folder + "/" + key);
        log.debug("删除" + bucketName + "下文件" + folder + "/" + key + "成功");
//        System.out.println("删除" + bucketName + "下文件" + folder + key + "成功");
    }

    private static OSSClient getOSSUtil() {
        OSSClient ossClient;
        try {
            ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            // 判断Bucket是否存在。详细请参看“SDK手册 > Java-SDK > 管理Bucket”。
            // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_bucket.html?spm=5176.docoss/sdk/java-sdk/init
            if (ossClient.doesBucketExist(bucketName)) {
                log.debug("您已经创建Bucket：" + bucketName + "。");
            } else {
                log.debug("您的Bucket不存在，创建Bucket：" + bucketName + "。");
                // 创建Bucket。详细请参看“SDK手册 > Java-SDK > 管理Bucket”。
                // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_bucket.html?spm=5176.docoss/sdk/java-sdk/init
                ossClient.createBucket(bucketName);
            }
        } catch (Exception e) {
            log.error("调用OSS api失败");
            return null;
        }
        return ossClient;
    }
}

