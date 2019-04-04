package com.tj.util.umeng;

import com.tj.util.constant.ConstantConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import push.AndroidNotification;
import push.Demo;
import push.PushClient;
import push.android.*;
import push.ios.*;

import java.util.Map;

@Service
public class PushUtil {
    private String adrAppkey = "5b4481a9f29d981a930000a8";
    private String adrAppMasterSecret = "227n0ks9dwjsfydn2ysgl14u4eshu51h";
    private String iosAppkey = "5b4481a9f29d981a930000a8";
    private String iosAppMasterSecret = "227n0ks9dwjsfydn2ysgl14u4eshu51h";
    private String timestamp = null;
    private String adrActivityUrl = "com.alphawizard.Wallet.activity.OuterBrowserActivity";
    private String iosActivityUrl = "";
    private PushClient client = new PushClient();

    public static void main(String[] args) {
        // TODO set your appkey and master secret here
        Demo demo = new Demo("your appkey", "the app master secret");
        try {

            /* TODO these methods are all available, just fill in some fields and do the test
             * demo.sendAndroidCustomizedcastFile();
             * demo.sendAndroidBroadcast();
             * demo.sendAndroidGroupcast();
             * demo.sendAndroidCustomizedcast();
             * demo.sendAndroidFilecast();
             *
             * demo.sendIOSBroadcast();
             * demo.sendIOSUnicast();
             * demo.sendIOSGroupcast();
             * demo.sendIOSCustomizedcast();
             * demo.sendIOSFilecast();
             */
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 广播(broadcast)默认每天可推送10次
     *
     * @throws Exception
     */
    public Boolean sendAndroidBroadcast(String ticker, String title, String text, Map<String, String> map) throws Exception {
        AndroidBroadcast broadcast = new AndroidBroadcast(adrAppkey, adrAppMasterSecret);
        broadcast.setTicker(ticker);
        broadcast.setTitle(title);
        broadcast.setText(text);

        broadcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
        // TODO Set 'production_mode' to 'false' if it's a test device.
        // For how to register a test device, please see the developer doc.
        if (ConstantConfig.ApplicationTest) {
            broadcast.setTestMode();
        } else {
            broadcast.setProductionMode();
        }
        broadcast.setMipushMode(true);
        broadcast.setMiActivityUrl(adrActivityUrl);
        // Set customized fields
        if (null != map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                broadcast.setExtraField(entry.getKey(), entry.getValue());
            }
            if (null != map.get("jumpUrl")) {
                broadcast.goActivityAfterOpen(adrActivityUrl);
            } else {
                broadcast.goAppAfterOpen();
            }
        } else {
            broadcast.goAppAfterOpen();
        }
        return client.send(broadcast);
    }

    /**
     * 单播(unicast): 向指定的设备发送消息
     *
     * @throws Exception
     */
    public Boolean sendAndroidUnicast(String deviceToke, String ticker, String title, String text, Map<String, String> map) throws Exception {
        AndroidUnicast unicast = new AndroidUnicast(adrAppkey, adrAppMasterSecret);
        // TODO Set your device token
        unicast.setDeviceToken(deviceToke);
        unicast.setTicker(ticker);
        unicast.setTitle(title);
        unicast.setText(text);

        unicast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
        // TODO Set 'production_mode' to 'false' if it's a test device.
        // For how to register a test device, please see the developer doc.
        if (ConstantConfig.ApplicationTest) {
            unicast.setTestMode();
        } else {
            unicast.setProductionMode();
        }

        unicast.setMipushMode(true);
        unicast.setMiActivityUrl(adrActivityUrl);
        // Set customized fields
        if (null != map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                unicast.setExtraField(entry.getKey(), entry.getValue());
            }
            if (null != map.get("jumpUrl")) {
                unicast.goActivityAfterOpen(adrActivityUrl);
            } else {
                unicast.goAppAfterOpen();
            }
        } else {
            unicast.goAppAfterOpen();
        }
        return client.send(unicast);
    }

    /**
     * 广播(broadcast): 向安装该App的所有设备发送消息,默认每分钟可推送5次
     *
     * @throws Exception
     */
    public void sendAndroidGroupcast() throws Exception {
        AndroidGroupcast groupcast = new AndroidGroupcast(adrAppkey, adrAppMasterSecret);
        /*  TODO
         *  Construct the filter condition:
         *  "where":
         *	{
         *		"and":
         *		[
         *			{"tag":"test"},
         *			{"tag":"Test"}
         *		]
         *	}
         */
        JSONObject filterJson = new JSONObject();
        JSONObject whereJson = new JSONObject();
        JSONArray tagArray = new JSONArray();
        JSONObject testTag = new JSONObject();
        JSONObject TestTag = new JSONObject();
        testTag.put("tag", "test");
        TestTag.put("tag", "Test");
        tagArray.put(testTag);
        tagArray.put(TestTag);
        whereJson.put("and", tagArray);
        filterJson.put("where", whereJson);
        System.out.println(filterJson.toString());

        groupcast.setFilter(filterJson);
        groupcast.setTicker("Android groupcast ticker");
        groupcast.setTitle("中文的title");
        groupcast.setText("Android groupcast text");
        groupcast.goAppAfterOpen();
        groupcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
        // TODO Set 'production_mode' to 'false' if it's a test device.
        // For how to register a test device, please see the developer doc.
        groupcast.setProductionMode();
        client.send(groupcast);
    }

    /**
     * 自定义播(customizedcast): 开发者通过自有的alias进行推送，可以针对单个或者一批alias进行推送，也可以将alias存放到文件进行发送。
     * (customizedcast, 且file_id不为空)默认每小时可推送300次
     *
     * @throws Exception
     */
    public void sendAndroidCustomizedcast() throws Exception {
        AndroidCustomizedcast customizedcast = new AndroidCustomizedcast(adrAppkey, adrAppMasterSecret);
        // TODO Set your alias here, and use comma to split them if there are multiple alias.
        // And if you have many alias, you can also upload a file containing these alias, then
        // use file_id to send customized notification.
        customizedcast.setAlias("alias", "alias_type");
        customizedcast.setTicker("Android customizedcast ticker");
        customizedcast.setTitle("中文的title");
        customizedcast.setText("Android customizedcast text");
        customizedcast.goAppAfterOpen();
        customizedcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
        // TODO Set 'production_mode' to 'false' if it's a test device.
        // For how to register a test device, please see the developer doc.
        customizedcast.setProductionMode();
        client.send(customizedcast);
    }

    public void sendAndroidCustomizedcastFile() throws Exception {
        AndroidCustomizedcast customizedcast = new AndroidCustomizedcast(adrAppkey, adrAppMasterSecret);
        // TODO Set your alias here, and use comma to split them if there are multiple alias.
        // And if you have many alias, you can also upload a file containing these alias, then
        // use file_id to send customized notification.
        String fileId = client.uploadContents(adrAppkey, adrAppMasterSecret, "aa" + "\n" + "bb" + "\n" + "alias");
        customizedcast.setFileId(fileId, "alias_type");
        customizedcast.setTicker("Android customizedcast ticker");
        customizedcast.setTitle("中文的title");
        customizedcast.setText("Android customizedcast text");
        customizedcast.goAppAfterOpen();
        customizedcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
        // TODO Set 'production_mode' to 'false' if it's a test device.
        // For how to register a test device, please see the developer doc.
        customizedcast.setProductionMode();
        client.send(customizedcast);
    }

    /**
     * 文件播(filecast): 开发者将批量的device_token或者alias存放到文件，通过文件ID进行消息发送。
     *
     * @throws Exception
     */
    public void sendAndroidFilecast() throws Exception {
        AndroidFilecast filecast = new AndroidFilecast(adrAppkey, adrAppMasterSecret);
        // TODO upload your device tokens, and use '\n' to split them if there are multiple tokens
        String fileId = client.uploadContents(adrAppkey, adrAppMasterSecret, "aa" + "\n" + "bb");
        filecast.setFileId(fileId);
        filecast.setTicker("Android filecast ticker");
        filecast.setTitle("中文的title");
        filecast.setText("Android filecast text");
        filecast.goAppAfterOpen();
        filecast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
        client.send(filecast);
    }

    public void sendIOSBroadcast(String ticker, String title, String text, Map<String, String> map) throws Exception {
        IOSBroadcast broadcast = new IOSBroadcast(iosAppkey, iosAppMasterSecret);


        broadcast.setAlert("IOS 广播测试");
        broadcast.setBadge(0);
        broadcast.setSound("default");
        // TODO set 'production_mode' to 'true' if your app is under production mode
        if (ConstantConfig.ApplicationTest) {
            broadcast.setTestMode();
        } else {
            broadcast.setProductionMode();
        }
        // Set customized fields
        broadcast.setCustomizedField("test", "helloworld");
        client.send(broadcast);
    }

    public Boolean sendIOSUnicast(String deviceToken, String ticker, String title, String text, Map<String, String> map) throws Exception {
        IOSUnicast unicast = new IOSUnicast(iosAppkey, iosAppMasterSecret);
        // TODO Set your device token
        unicast.setDeviceToken("xx");
        unicast.setAlert("IOS 单播测试");
        unicast.setBadge(0);
        unicast.setSound("default");
        // TODO set 'production_mode' to 'true' if your app is under production mode
        if (ConstantConfig.ApplicationTest) {
            unicast.setTestMode();
        } else {
            unicast.setProductionMode();
        }
        // Set customized fields
        unicast.setCustomizedField("test", "helloworld");
        return client.send(unicast);
    }

    public void sendIOSGroupcast() throws Exception {
        IOSGroupcast groupcast = new IOSGroupcast(iosAppkey, iosAppMasterSecret);
        /*  TODO
         *  Construct the filter condition:
         *  "where":
         *	{
         *		"and":
         *		[
         *			{"tag":"iostest"}
         *		]
         *	}
         */
        JSONObject filterJson = new JSONObject();
        JSONObject whereJson = new JSONObject();
        JSONArray tagArray = new JSONArray();
        JSONObject testTag = new JSONObject();
        testTag.put("tag", "iostest");
        tagArray.put(testTag);
        whereJson.put("and", tagArray);
        filterJson.put("where", whereJson);
        System.out.println(filterJson.toString());

        // Set filter condition into rootJson
        groupcast.setFilter(filterJson);
        groupcast.setAlert("IOS 组播测试");
        groupcast.setBadge(0);
        groupcast.setSound("default");
        // TODO set 'production_mode' to 'true' if your app is under production mode
        groupcast.setTestMode();
        client.send(groupcast);
    }

    public void sendIOSCustomizedcast() throws Exception {
        IOSCustomizedcast customizedcast = new IOSCustomizedcast(iosAppkey, iosAppMasterSecret);
        // TODO Set your alias and alias_type here, and use comma to split them if there are multiple alias.
        // And if you have many alias, you can also upload a file containing these alias, then
        // use file_id to send customized notification.
        customizedcast.setAlias("alias", "alias_type");
        customizedcast.setAlert("IOS 个性化测试");
        customizedcast.setBadge(0);
        customizedcast.setSound("default");
        // TODO set 'production_mode' to 'true' if your app is under production mode
        customizedcast.setTestMode();
        client.send(customizedcast);
    }

    public void sendIOSFilecast() throws Exception {
        IOSFilecast filecast = new IOSFilecast(iosAppkey, iosAppMasterSecret);
        // TODO upload your device tokens, and use '\n' to split them if there are multiple tokens
        String fileId = client.uploadContents(iosAppkey, iosAppMasterSecret, "aa" + "\n" + "bb");
        filecast.setFileId(fileId);
        filecast.setAlert("IOS 文件播测试");
        filecast.setBadge(0);
        filecast.setSound("default");
        // TODO set 'production_mode' to 'true' if your app is under production mode
        filecast.setTestMode();
        client.send(filecast);
    }

}

