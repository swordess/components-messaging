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

class BaiduPushMessage : Message {

    lateinit var req: PushRequest

    private val body = mutableMapOf<String, Any?>()

    var PushMsgToSingleDeviceRequest.title by body
    var PushMsgToSingleDeviceRequest.description by body
    var PushMsgToSingleDeviceRequest.custom_content by body

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

fun baiduPushMessage(reqBuilder: BaiduPushMessage.() -> PushRequest): BaiduPushMessage {
    val msg = BaiduPushMessage()
    msg.req = msg.reqBuilder()
    return msg
}
