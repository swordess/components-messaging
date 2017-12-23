package com.mobisist.components.messaging.baidupush

import com.baidu.yun.push.auth.PushKeyPair
import com.baidu.yun.push.client.BaiduPushClient
import com.baidu.yun.push.exception.PushClientException
import com.baidu.yun.push.exception.PushServerException
import com.baidu.yun.push.model.*
import com.mobisist.components.messaging.Message
import com.mobisist.components.messaging.MessageSender
import com.mobisist.components.messaging.MessagingException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

sealed class BaiduPushConfig {

    lateinit var apiKey: String
    lateinit var secretKey: String

    class AndroidPushConfig : BaiduPushConfig()

    class IOSPushConfig : BaiduPushConfig() {
        lateinit var deployStatus: IOSDeployStatus
    }

}

class BaiduPushMessagingException : MessagingException {
    constructor(cause: Throwable) : super(cause)
    constructor(msg: String) : super(msg)
}

open class BaiduPushSender : MessageSender<BaiduPushMessage, Unit> {
    private val logger: Logger = LoggerFactory.getLogger(BaiduPushSender::class.java)

    private val androidClientCaches = ConcurrentHashMap<String, BaiduPushClient>()
    private val iosClientCaches = ConcurrentHashMap<String, BaiduPushClient>()

    lateinit var androidConfigProvider: (String) -> BaiduPushConfig.AndroidPushConfig
    lateinit var iosConfigProvider: (String) -> BaiduPushConfig.IOSPushConfig

    private fun androidClientFor(config: String) = androidClientCaches.getOrPut(config) {
        val androidConfig = androidConfigProvider(config)
        BaiduPushClient(PushKeyPair(androidConfig.apiKey, androidConfig.secretKey)).apply {
            setChannelLogHandler {
                logger.debug(it.message)
            }
        }
    }

    private fun iosClientFor(config: String) = iosClientCaches.getOrPut(config) {
        val iosConfig = iosConfigProvider(config)
        BaiduPushClient(PushKeyPair(iosConfig.apiKey, iosConfig.secretKey)).apply {
            setChannelLogHandler {
                logger.debug(it.message)
            }
        }
    }

    override fun canHandle(msg: Message): Boolean = msg is BaiduPushMessage

    @Throws(BaiduPushMessagingException::class)
    override fun send(msg: BaiduPushMessage) {
        try {
            val client = when (msg) {
                is BaiduPushMessage.AndroidPushMessage -> androidClientFor(msg.config)
                is BaiduPushMessage.IOSPushMessage -> iosClientFor(msg.config)
            }

            val req = msg.req
            when (req) {
                is PushMsgToSingleDeviceRequest -> sendSingleMsg(client, req)
                is PushMsgToTagRequest -> sendTagMsg(client, req)
                is PushMsgToAllRequest -> sendBroadcastMsg(client, req)
                else -> throw BaiduPushMessagingException("unsupported req type: ${msg.req.javaClass}")
            }
        } catch (e: BaiduPushMessagingException) {
            throw e
        } catch (e: PushClientException) {
            logger.error("cannot push", e)
        } catch (e: PushServerException) {
            // 30608: Bind Relation Not Found 绑定关系未找到或不存在
            // See: http://push.baidu.com/doc/restapi/error_code
            when (e.errorCode) {
                30608 -> logger.info("requestId: ${e.requestId}, errorCode=${e.errorCode}, errorMsg: ${e.errorMsg}")
                else -> logger.error("requestId: ${e.requestId}, errorCode=${e.errorCode}, errorMsg: ${e.errorMsg}", e)
            }
        }
    }

    private fun sendSingleMsg(client: BaiduPushClient, msg: PushMsgToSingleDeviceRequest) {
        val resp = client.pushMsgToSingleDevice(msg)
        logger.debug("msgId: ${resp.msgId}, sendTime: ${resp.sendTime}")
    }

    private fun sendTagMsg(client: BaiduPushClient, msg: PushMsgToTagRequest) {
        val resp = client.pushMsgToTag(msg)
        logger.debug("msgId: ${resp.msgId}, sendTime: ${resp.sendTime}")
    }

    private fun sendBroadcastMsg(client: BaiduPushClient, msg: PushMsgToAllRequest) {
        val resp = client.pushMsgToAll(msg)
        logger.debug("msgId: ${resp.msgId}, sendTime: ${resp.sendTime}")
    }

    @JvmOverloads
    fun buildMsgToAndroid(config: String = "default", reqBuilder: BaiduPushMessage.AndroidMsgBuilder.() -> PushRequest): BaiduPushMessage.AndroidPushMessage {
        return BaiduPushMessage.AndroidPushMessage(config).apply {
            req = BaiduPushMessage.AndroidMsgBuilder(androidConfigProvider(config)).reqBuilder()
        }
    }

    @JvmOverloads
    fun buildMsgToIOS(config: String = "default", reqBuilder: BaiduPushMessage.IOSMsgBuilder.() -> PushRequest): BaiduPushMessage.IOSPushMessage {
        return BaiduPushMessage.IOSPushMessage(config).apply {
            req = BaiduPushMessage.IOSMsgBuilder(iosConfigProvider(config)).reqBuilder()
        }
    }

    fun deleteDeviceFromTag(config: String = "default", deviceType: DeviceType, tagName: String, vararg channelIds: String) {
        val client = when (deviceType) {
            DeviceType.ANDROID ->
                androidClientFor(config)
            DeviceType.IOS ->
                iosClientFor(config)
        }
        val request = DeleteDevicesFromTagRequest().apply {
            this.tagName = tagName
            this.setChannelIds(channelIds)
            this.setDeviceType(deviceType.rawValue)
        }
        try {
            val response = client.deleteDevicesFromTag(request)
            logger.debug(response.toString())
        } catch (e: BaiduPushMessagingException) {
            throw e
        } catch (e: PushClientException) {
            logger.error("cannot push", e)
        } catch (e: PushServerException) {
            // 30608: Bind Relation Not Found 绑定关系未找到或不存在
            // See: http://push.baidu.com/doc/restapi/error_code
            when (e.errorCode) {
                30608 -> logger.info("requestId: ${e.requestId}, errorCode=${e.errorCode}, errorMsg: ${e.errorMsg}")
                else -> logger.error("requestId: ${e.requestId}, errorCode=${e.errorCode}, errorMsg: ${e.errorMsg}", e)
            }
        }
    }

}