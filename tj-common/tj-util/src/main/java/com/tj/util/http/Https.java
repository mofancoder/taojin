package com.tj.util.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Closer;
import com.tj.util.Docs;
import com.tj.util.Results;
import com.tj.util.WebConfig;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * http请求工具类
 *
 * @author yelo
 * @date 2015/10/25
 */

public class Https {

    public static String get(String url) throws IOException {
        return http(url, new Function<String, HttpUriRequest>() {
            @Override
            public HttpUriRequest apply(String input) {
                HttpGet get = new HttpGet(input);
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36");
                return get;
            }
        });
    }

    public static byte[] getImage(String url) throws IOException {
        return httpToByte(url, new Function<String, HttpUriRequest>() {
            @Override
            public HttpUriRequest apply(String input) {
                return new HttpGet(input);
            }
        });

    }

    public static String delete(final String url) throws IOException {
        return http(url, new Function<String, HttpUriRequest>() {
            @Override
            public HttpUriRequest apply(String input) {
                return new HttpDelete(input);
            }
        });
    }

    public static String put(final String url, final List<NameValuePair> pars) throws IOException {
        return http(url, new Function<String, HttpUriRequest>() {
            @Override
            public HttpUriRequest apply(String input) {
                HttpPut httpPut = new HttpPut(url);
                if (pars != null && !pars.isEmpty()) {
                    httpPut.setEntity(new UrlEncodedFormEntity(pars, Charsets.UTF_8));
                }
                return httpPut;
            }
        });
    }

    public static String put(final String url, final NameValuePair... pars) throws IOException {
        return put(url, Lists.newArrayList(pars));
    }

    public static String put(final String url, final String body) throws IOException {
        return http(url, new Function<String, HttpUriRequest>() {
            @Override
            public HttpUriRequest apply(String input) {
                HttpPut httpPut = new HttpPut(url);
                httpPut.setEntity(new StringEntity(body, Charsets.UTF_8));
                return httpPut;
            }
        });
    }

    public static String put(final String url, final Map<String, String> params) throws IOException {
        Iterator<String> iterator = params.keySet().iterator();
        List<NameValuePair> pList = Lists.newArrayList();
        while (iterator.hasNext()) {
            String key = iterator.next();
            pList.add(new BasicNameValuePair(key, params.get(key)));
        }
        return put(url, pList);
    }

    public static String post(final String url, final NameValuePair... pars) throws IOException {
        List<NameValuePair> pList = Lists.newArrayList(pars);
        return post(url, pList);
    }

    public static String post(final String url, final List<NameValuePair> pList) throws IOException {
        return http(url, new Function<String, HttpUriRequest>() {
            @Override
            public HttpUriRequest apply(String input) {
                HttpPost httpPost = new HttpPost(input);
                if (pList != null && !pList.isEmpty()) {
                    httpPost.setEntity(new UrlEncodedFormEntity(pList, Charsets.UTF_8));
                }
                return httpPost;
            }
        });
    }

    public static String post(final String url, final Map<String, String> params) throws IOException {
        Iterator<String> iterator = params.keySet().iterator();
        List<NameValuePair> pList = Lists.newArrayList();
        while (iterator.hasNext()) {
            String key = iterator.next();
            pList.add(new BasicNameValuePair(key, params.get(key)));
        }
        return post(url, pList);
    }

    public static String post(String url, final String body) throws IOException {
        return http(url, new Function<String, HttpUriRequest>() {
            @Override
            public HttpUriRequest apply(String input) {
                HttpPost httpPost = new HttpPost(input);
                httpPost.setEntity(new StringEntity(body, Charsets.UTF_8));
                return httpPost;
            }
        });
    }

    private static String http(String url, Function<String, HttpUriRequest> func) throws IOException {
        if (StringUtils.isEmpty(url) || func == null) {
            return null;
        }
        Closer closer = Closer.create();


        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(10000)
                .setConnectTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();

        HttpUriRequest http = func.apply(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(http);
            closer.register(httpClient);
            closer.register(response);
            //if (response.getStatusLine().getStatusCode() == 200) {
            return EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
            // }

        } catch (IOException e) {
            closer.rethrow(e);
        } finally {
            closer.close();
        }
        return null;
    }

    private static byte[] httpToByte(String url, Function<String, HttpUriRequest> func) throws IOException {
        if (StringUtils.isEmpty(url) || func == null) {
            return null;
        }
        Closer closer = Closer.create();


        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(10000)
                .setConnectTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();

        HttpUriRequest http = func.apply(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(http);
            closer.register(httpClient);
            closer.register(response);

            //if (response.getStatusLine().getStatusCode() == 200) {
            if (response.getEntity().isStreaming()) {
                InputStream in = response.getEntity().getContent();
                byte[] data = new byte[1024];
                int len = 0;
                ByteArrayBuffer buffer = new ByteArrayBuffer(1024 * 60);
                while (-1 != (len = in.read(data))) {
                    buffer.append(data, 0, len);
                }
                return buffer.toByteArray();
            } else {
                return null;
            }
            // }

        } catch (IOException e) {
            closer.rethrow(e);
        } finally {
            closer.close();
        }
        return null;
    }

    /**
     * @param file
     * @return 图片路径
     * @throws IOException
     */
    public static String uploadImage(MultipartFile file) {
        return uploadImage(file, 220, 220);
    }

    /**
     * @param file
     * @return 图片路径
     * @throws IOException
     */
    public static String uploadImageNormal(MultipartFile file) {
        return uploadImage(file, 600, 600);
    }


    /**
     * @param file
     * @return 图片路径
     * @throws IOException
     */
    public static String uploadImage(MultipartFile file, int width, int height) {
        File f = null;
        try {
            f = new File(Docs.png());
            //强制转成png格式，导致图片大小变大很多，不建议使用
            //Thumbnails.of(file.getInputStream()).outputFormat("png").size(width,height).toFile(f);
            //outputQuality(0.8f) 参数1为最高质量
            Thumbnails.of(file.getInputStream()).size(width, height).outputQuality(1f).toFile(f);
            Map<String, File> map = Maps.newHashMap();
            map.put("file", f);
            String result = upload(WebConfig.DEFAULT_UPLOAD_IMAGE_URL, map);
            JSONObject jsonObject = JSON.parseObject(result);
            if ("200".equals(jsonObject.getString("code"))) {
                String path = jsonObject.getJSONObject("result").getJSONObject("file").getString("url");
                return path;
            } else {
                throw new RuntimeException(result);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (f != null) {
                f.delete();
            }
        }
    }

    public static String uploadImage(MultipartFile file, int width, int height, String itemId) {
        File f = null;
        try {
            f = new File(itemId + ".jpg");
            //f = new File(Docs.png());
            //强制转成png格式，导致图片大小变大很多，不建议使用
            //Thumbnails.of(file.getInputStream()).outputFormat("png").size(width,height).toFile(f);
            //outputQuality(0.8f) 参数1为最高质量
            Thumbnails.of(file.getInputStream()).size(width, height).outputFormat("jpg").outputQuality(0.8f).toFile(f);
            Map<String, File> map = Maps.newHashMap();
            map.put("file", f);
            String result = upload(WebConfig.DEFAULT_UPLOAD_IMAGE_URL, map);
            JSONObject jsonObject = JSON.parseObject(result);
            if ("200".equals(jsonObject.getString("code"))) {
                String path = jsonObject.getJSONObject("result").getJSONObject("file").getString("url");
                return path;
            } else {
                throw new RuntimeException(result);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (f != null) {
                f.delete();
            }
        }
    }

    public static String uploadNotice(MultipartFile file) {
        File f = null;
        try {
            f = new File(Docs.png());
            FileUtils.copyInputStreamToFile(file.getInputStream(), f);
            Map<String, File> map = Maps.newHashMap();
            map.put("file", f);
            String result = upload(WebConfig.DEFAULT_UPLOAD_IMAGE_URL, map);
            JSONObject jsonObject = JSON.parseObject(result);
            if ("200".equals(jsonObject.getString("code"))) {
                String path = jsonObject.getJSONObject("result").getJSONObject("file").getString("url");
                return path;
            } else {
                throw new RuntimeException(result);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (f != null) {
                f.deleteOnExit();
            }
        }
    }


    public static String upload(String url, final MultipartFile... files) throws IOException {
        if (!StringUtils.isEmpty(url)) {
            return JSON.toJSONString(Results.of().put("code", "8899").put("msg", "文件不能为空").toMap());
        }
        return http(url, new Function<String, HttpUriRequest>() {
            @Override
            public HttpUriRequest apply(String s) {
                HttpPost httpPost = new HttpPost(s);
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                for (MultipartFile file : files) {
                    try {
                        multipartEntityBuilder.addPart(file.getName(), new InputStreamBody(file.getInputStream(), file.getOriginalFilename()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                httpPost.setEntity(multipartEntityBuilder.build());
                return httpPost;
            }
        });
    }

    public static String upload(String url, final Map<String, File> fieldMap) throws IOException {

        if (fieldMap.isEmpty())
            return JSON.toJSONString(Results.of().put("code", "8899").put("msg", "文件不能为空").toMap());
        return http(url, new Function<String, HttpUriRequest>() {
            @Override
            public HttpUriRequest apply(String s) {
                HttpPost httpPost = new HttpPost(s);
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                for (Map.Entry<String, File> entry : fieldMap.entrySet()) {
                    multipartEntityBuilder.addPart(entry.getKey(), new FileBody(entry.getValue()));
                }
                httpPost.setEntity(multipartEntityBuilder.build());
                return httpPost;
            }
        });
    }

    /**
     * @param file
     * @return 图片路径 [原图地址，缩图地址]
     * @throws IOException
     */
    public static String[] uploadImageMiddleAndThumb(MultipartFile file) {
        try {
            //上传缩图
            String thumbnailsUrl = uploadImage(file, 220, 220);
            //上传源文件
            String originalUrl = uploadImage(file, 640, 640);
            return new String[]{originalUrl, thumbnailsUrl};
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param file
     * @return 图片路径 [原图地址，缩图地址]
     * @throws IOException
     */
    public static String[] uploadImageMiddleAndThumb(MultipartFile file, String itemId) {
        try {
            //上传缩图
            String thumbnailsUrl = uploadImage(file, 220, 220, itemId);
            //上传源文件
            String originalUrl = uploadImage(file, 640, 640, itemId);
            return new String[]{originalUrl, thumbnailsUrl};
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getIpLocation(String ip) {
        try {
            String jsonStr = Https.get("https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?query=" + ip + "&resource_id=6006");
            JSONObject ipjson = JSONObject.parseObject(jsonStr);
            if (ipjson.containsKey("data")) {
                JSONArray ipArray = ipjson.getJSONArray("data");
                if (!ipArray.isEmpty() && ipArray.size() > 0) {
                    JSONObject item = ipArray.getJSONObject(0);
                    String location = item.getString("location");
                    return location;
                }
            }
        } catch (IOException e) {
            return "未知";
        }
        return "未知";
    }


    public static void main(String[] a) {
        try {
            //byte[] data = Https.getImage("https://sac-test.oss-cn-beijing.aliyuncs.com/userInfo/5a2ad21689d8edfa519704de70d9329e.jpg");
			/*File file = new File("D://11.jpg");
			FileOutputStream out = new FileOutputStream(file);
			out.write(data);
			out.close();*/
            //CertificateUtil.CertificateFacePic(data);
            //System.out.println(json);
            String data = Https.get("https://www.chaoex.io/12lian/quote/realTime?baseCurrencyId=3&tradeCurrencyId=74");
            System.out.println("输出:" + data);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
