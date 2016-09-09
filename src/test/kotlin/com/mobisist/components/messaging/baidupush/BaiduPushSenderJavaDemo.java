package com.mobisist.components.messaging.baidupush;

import kotlin.Unit;

import java.util.HashMap;
import java.util.Map;

public class BaiduPushSenderJavaDemo {

    public static void main(String[] args) {
        BaiduPushConfig config = new BaiduPushConfig();
        config.setApiKey("gv0ft0gzCdO19FL5OgYIqYG9");
        config.setSecretKey("zrRgdC2LlPancB3yxrmacyosdtLh3k92");

        BaiduPushSender sender = new BaiduPushSender();
        sender.setConfig(config);

        BaiduPushMessage msg = BaiduPushMessageKt.baiduPushMessage(create ->
                create.pushMsgToSingleDeviceRequest(req -> {
                    req.setChannelId("3478330428537063857");
                    req.setMessageType(MessageType.NOTIFICATION.getIntValue());
                    req.setDeviceType(DeviceType.ANDROID.getIntValue());
                    // 5 mins
                    req.setMsgExpires(300);

                    create.setTitle(req, "Title goes here");
                    create.setDescription(req, "Description goes here");

                    Map<String, Object> customContent = new HashMap<>();
                    customContent.put("myKey", "myValue");
                    create.setCustom_content(req, customContent);
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
