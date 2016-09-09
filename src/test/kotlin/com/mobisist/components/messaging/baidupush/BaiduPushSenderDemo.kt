package com.mobisist.components.messaging.baidupush

fun main(args: Array<String>) {
    val sender = BaiduPushSender().apply {
        config = BaiduPushConfig().apply {
            apiKey = "gv0ft0gzCdO19FL5OgYIqYG9"
            secretKey = "zrRgdC2LlPancB3yxrmacyosdtLh3k92"
        }
    }

    val msg = baiduPushMessage {
        pushMsgToSingleDeviceRequest {
            channelId = "3478330428537063857"
            messageType = MessageType.NOTIFICATION.intValue
            setDeviceType(DeviceType.ANDROID.intValue)
            // 5 mins
            msgExpires = 300

            title = "Title goes here"
            description = "Description goes here"

            // customized body when send to android devices
            custom_content = mapOf(
                    "myKey" to "myValue"
            )
        }
    }

    try {
        sender.send(msg)
    } catch (e: BaiduPushMessagingException) {
        // you can handle exception here
    }
}