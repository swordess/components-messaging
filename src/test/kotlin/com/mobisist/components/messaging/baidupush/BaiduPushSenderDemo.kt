package com.mobisist.components.messaging.baidupush

fun main(args: Array<String>) {
    sendAndroidPush()
    sendIOSPush()
    deleteDeviceFromTag()
}

private fun sendAndroidPush() {
    val sender = BaiduPushSender().apply {
        androidConfigProvider = {
            BaiduPushConfig.AndroidPushConfig().apply {
                apiKey = "G0q2pyhUZ2if01LDs70ccZIpcNMnj7NU"
                secretKey = "w8etzvSbcmXTmlVMP7tkoCPbUiBPiXhj"
            }
        }
    }

    val msg = sender.buildMsgToAndroid {
        pushMsgToSingleDeviceRequest {
            channelId = "3478330428537063857"
            messageType = MessageType.NOTIFICATION.rawValue
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

    val tagMsg = sender.buildMsgToAndroid {
        pushMsgToTagRequest {
            tagName = "t-xiaoguan-2"
            messageType = MessageType.NOTIFICATION.rawValue
            setDeviceType(DeviceType.ANDROID.rawValue)
            // 5 mins
            msgExpires = 300

            title = "this is a tag msg"
            description = "Tag msg test"

            // customized body when send to android devices
            custom_content = mapOf(
                    "myKey" to "myValue"
            )
        }
    }

    val allMsg = sender.buildMsgToAndroid {
        pushMsgToAllRequest {
            messageType = MessageType.NOTIFICATION.rawValue
            setDeviceType(DeviceType.ANDROID.rawValue)
            // 5 mins
            msgExpires = 300

            title = "this is a broadcast msg"
            description = "broadcast msg test"

            // customized body when send to android devices
            custom_content = mapOf(
                    "myKey" to "myValue"
            )
        }
    }

    try {
        sender.send(msg)
        sender.send(tagMsg)
        sender.send(allMsg)
    } catch (e: BaiduPushMessagingException) {
        // you can handle exception here
        println(e)
    }
}

private fun sendIOSPush() {
    val sender = BaiduPushSender().apply {
        iosConfigProvider = {
            BaiduPushConfig.IOSPushConfig().apply {
                apiKey = "gv0ft0gzCdO19FL5OgYIqYG9"
                secretKey = "zrRgdC2LlPancB3yxrmacyosdtLh3k92"
                deployStatus = IOSDeployStatus.DEVELOPMENT
            }
        }
    }

    val msg = sender.buildMsgToIOS {
        pushMsgToSingleDeviceRequest {
            channelId = "3478330428537063857"
            messageType = MessageType.NOTIFICATION.rawValue
            // 5 mins
            msgExpires = 300

            alert = "Title goes here"
            sound = "default"

            "key1" setTo "value1"
            "key2" setTo "value2"
        }
    }
    try {
        sender.send(msg)
    } catch (e: BaiduPushMessagingException) {
        // you can handle exception here
    }
}

private fun deleteDeviceFromTag() {
    val sender = BaiduPushSender().apply {
        androidConfigProvider = {
            BaiduPushConfig.AndroidPushConfig().apply {
                apiKey = "G0q2pyhUZ2if01LDs70ccZIpcNMnj7NU"
                secretKey = "w8etzvSbcmXTmlVMP7tkoCPbUiBPiXhj"
            }
        }
    }
    sender.deleteDeviceFromTag("default", DeviceType.ANDROID, "t-xiaoguan-13", "3460647103306423663")
}
