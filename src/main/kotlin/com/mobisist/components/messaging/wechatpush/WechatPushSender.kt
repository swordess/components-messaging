package com.mobisist.components.messaging.wechatpush

import com.mobisist.components.messaging.Message
import com.mobisist.components.messaging.MessageSender
import com.mobisist.components.messaging.MessagingException
import me.chanjar.weixin.common.exception.WxErrorException
import me.chanjar.weixin.mp.api.WxMpConfigStorage
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.api.WxMpServiceImpl
import me.chanjar.weixin.mp.bean.WxMpTemplateData
import me.chanjar.weixin.mp.bean.WxMpTemplateMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class WechatPushMessagingException(cause: Throwable) : MessagingException(cause)

open class WechatPushSender : MessageSender<WechatPushMessage, Unit> {

    protected val logger: Logger = LoggerFactory.getLogger(WechatPushSender::class.java)

    var templateIdProvider: TemplateIdProvider? = null

    var wxMpConfigStorage: WxMpConfigStorage? = null

    var wxMpService: WxMpService? = null

    private val __wxMpService: WxMpService? by lazy {
        if (wxMpService != null) {
            return@lazy wxMpService
        } else if (wxMpConfigStorage != null) {
            return@lazy WxMpServiceImpl().apply {
                setWxMpConfigStorage(wxMpConfigStorage)
            }
        } else {
            throw MessagingException("either wxMpConfigStorage or wxMpService should be configured")
        }
    }

    override fun canHandle(msg: Message): Boolean = msg is WechatPushMessage

    @Throws(WechatPushMessagingException::class)
    override fun send(msg: WechatPushMessage) {
        logger.debug("""prepare to send to ${msg.openId}, with {
        |    templateKey: ${msg.templateKey},
        |    templateData: ${msg.templateData}
        |}""".trimMargin())

        val wxMsg = WxMpTemplateMessage()
        if (msg.url.isNotEmpty()) {
            wxMsg.url = msg.url
        }
        wxMsg.toUser = msg.openId
        wxMsg.templateId = if (templateIdProvider != null) templateIdProvider!!.get(msg.templateKey) else msg.templateKey

        for ((name, data) in msg.templateData) {
            wxMsg.datas.add(WxMpTemplateData(name, data.value, data.color))
        }

        try {
            __wxMpService!!.templateSend(wxMsg)
        } catch (e: WxErrorException) {
            logger.error("send message error, ${e.message}")
            throw WechatPushMessagingException(e)
        }
    }

}