package com.mobisist.components.messaging.baidupush

import com.baidu.yun.push.model.PushMsgToSingleDeviceRequest
import com.baidu.yun.push.model.PushRequest
import com.google.gson.Gson
import com.mobisist.components.messaging.Message

enum class MessageType(val intValue: Int) {

    // 透传消息
    MESSAGE(0),

    // 通知
    NOTIFICATION(1)

}

enum class DeviceType(val intValue: Int) {
    ANDROID(3),
    IOS(4)
}

sealed class BaiduPushMessage : Message {

    lateinit var req: PushRequest

    class AndroidPushMessage : BaiduPushMessage()
    class IosPushMessage : BaiduPushMessage()

    open class MsgBuilder {

        internal val body = mutableMapOf<String, Any?>()

        @JvmOverloads
        fun pushMsgToSingleDeviceRequest(generateMsg: Boolean = true,
                                         init: PushMsgToSingleDeviceRequest.() -> Unit): PushMsgToSingleDeviceRequest {
            val req = PushMsgToSingleDeviceRequest()
            req.init()
            if (generateMsg) {
                req.message = Gson().toJson(body)
            }
            return req
        }

    }

    class AndroidMsgBuilder : MsgBuilder() {
        var title: String by body
        var description: String by body
        var custom_content: Map<String, Any> by body
    }

    class IOSMsgBuilder : MsgBuilder() {

        private val aps: MutableMap<String, Any> = body.getOrPut("aps", { mutableMapOf<String, Any>() }) as MutableMap<String, Any>

        var alert: String by aps
        var sound: String by aps
        var badge: Int by aps

        infix fun String.setTo(value: Any) {
            body[this] = value
        }

    }

    companion object {

        @JvmStatic
        fun toAndroid(reqBuilder: AndroidMsgBuilder.() -> PushRequest) = AndroidPushMessage().apply { req = AndroidMsgBuilder().reqBuilder() }

        @JvmStatic
        fun toIos(reqBuilder: IOSMsgBuilder.() -> PushRequest) = IosPushMessage().apply { req = IOSMsgBuilder().reqBuilder() }
    }

}
