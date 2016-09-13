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
        BaiduPushConfig config = new BaiduPushConfig();
        config.setApiKey("gv0ft0gzCdO19FL5OgYIqYG9");
        config.setSecretKey("zrRgdC2LlPancB3yxrmacyosdtLh3k92");

        BaiduPushSender sender = new BaiduPushSender();
        sender.setAndroidConfig(config);

        BaiduPushMessage msg = BaiduPushMessage.toAndroid(dsl ->
                dsl.pushMsgToSingleDeviceRequest(req -> {
                    req.setChannelId("3478330428537063857");
                    req.setMessageType(MessageType.NOTIFICATION.getIntValue());
                    req.setDeviceType(DeviceType.ANDROID.getIntValue());
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
        BaiduPushConfig config = new BaiduPushConfig();
        config.setApiKey("gv0ft0gzCdO19FL5OgYIqYG9");
        config.setSecretKey("zrRgdC2LlPancB3yxrmacyosdtLh3k92");

        BaiduPushSender sender = new BaiduPushSender();
        sender.setIosConfig(config);

        BaiduPushMessage msg = BaiduPushMessage.toIos(dsl ->
                dsl.pushMsgToSingleDeviceRequest(req -> {
                    req.setChannelId("3478330428537063857");
                    req.setMessageType(MessageType.NOTIFICATION.getIntValue());
                    req.setDeviceType(DeviceType.ANDROID.getIntValue());
                    // 5 mins
                    req.setMsgExpires(300);
                    req.setDeployStatus(1);

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
