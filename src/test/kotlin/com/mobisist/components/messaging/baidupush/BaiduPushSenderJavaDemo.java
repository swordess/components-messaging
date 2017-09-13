package com.mobisist.components.messaging.baidupush;

import kotlin.Unit;

import java.util.HashMap;
import java.util.Map;

public class BaiduPushSenderJavaDemo {

    public static void main(String[] args) {
        sendAndroidPush();
        sendIosPush();
    }

    private static void sendAndroidPush() {
        BaiduPushConfig.AndroidPushConfig defaultConfig = new BaiduPushConfig.AndroidPushConfig();
        defaultConfig.setApiKey("gv0ft0gzCdO19FL5OgYIqYG9");
        defaultConfig.setSecretKey("zrRgdC2LlPancB3yxrmacyosdtLh3k92");

        BaiduPushSender sender = new BaiduPushSender();
        sender.setAndroidConfigProvider((config) -> defaultConfig);

        BaiduPushMessage msg = sender.buildMsgToAndroid(dsl ->
                dsl.pushMsgToSingleDeviceRequest(req -> {
                    req.setChannelId("3478330428537063857");
                    req.setMessageType(MessageType.NOTIFICATION.getIntValue());
                    // 5 mins
                    req.setMsgExpires(300);

                    dsl.setTitle("Title goes here");
                    dsl.setDescription("Description goes here");

                    Map<String, Object> customContent = new HashMap<>();
                    customContent.put("myKey", "myValue");
                    dsl.setCustom_content(customContent);
                    return Unit.INSTANCE;
                })
        );

        try {
            sender.send(msg);
        } catch (BaiduPushMessagingException e) {
            // you can handle exception here
        }
    }

    private static void sendIosPush() {
        BaiduPushConfig.IOSPushConfig defaultConfig = new BaiduPushConfig.IOSPushConfig();
        defaultConfig.setApiKey("gv0ft0gzCdO19FL5OgYIqYG9");
        defaultConfig.setSecretKey("zrRgdC2LlPancB3yxrmacyosdtLh3k92");
        defaultConfig.setDeployStatus(IOSDeployStatus.DEVELOPMENT);

        BaiduPushSender sender = new BaiduPushSender();
        sender.setIosConfigProvider((config) -> defaultConfig);

        BaiduPushMessage msg = sender.buildMsgToIos(dsl ->
                dsl.pushMsgToSingleDeviceRequest(req -> {
                    req.setChannelId("3478330428537063857");
                    req.setMessageType(MessageType.NOTIFICATION.getIntValue());
                    // 5 mins
                    req.setMsgExpires(300);

                    dsl.setAlert("Title goes here");
                    dsl.setSound("default");

                    dsl.setTo("key1", "value1");
                    dsl.setTo("key2", "value2");
                    return Unit.INSTANCE;
                })
        );

        try {
            sender.send(msg);
        } catch (BaiduPushMessagingException e) {
            // you can handle exception here
        }
    }

}
