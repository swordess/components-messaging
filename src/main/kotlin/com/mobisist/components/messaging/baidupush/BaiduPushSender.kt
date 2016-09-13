package com.mobisist.components.messaging.baidupush

import com.baidu.yun.push.auth.PushKeyPair
import com.baidu.yun.push.client.BaiduPushClient
import com.baidu.yun.push.exception.PushClientException
import com.baidu.yun.push.exception.PushServerException
import com.baidu.yun.push.model.PushMsgToSingleDeviceRequest
import com.mobisist.components.messaging.Message
import com.mobisist.components.messaging.MessageSender
import com.mobisist.components.messaging.MessagingException
import org.slf4j.LoggerFactory

class BaiduPushConfig {
    lateinit var apiKey: String
    lateinit var secretKey: String
}

class BaiduPushMessagingException : MessagingException {
    constructor(cause: Throwable) : super(cause)
    constructor(msg: String) : super(msg)
}

class BaiduPushSender : MessageSender<BaiduPushMessage, Unit> {

    private val logger = LoggerFactory.getLogger(BaiduPushSender::class.java)

    lateinit var androidConfig: BaiduPushConfig
    lateinit var iosConfig: BaiduPushConfig

    private val androidClient: BaiduPushClient by lazy {
        BaiduPushClient(PushKeyPair(androidConfig.apiKey, androidConfig.secretKey)).apply {
            setChannelLogHandler {
                logger.debug(it.message)
            }
        }
    }

    private val iosClient: BaiduPushClient by lazy {
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
            val client = when(msg) {
                is BaiduPushMessage.AndroidPushMessage -> androidClient
                is BaiduPushMessage.IosPushMessage -> iosClient
            }

            val req = msg.req
            when (req) {
                is PushMsgToSingleDeviceRequest -> sendSingleMsg(client, req)
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

}