package com.mobisist.components.messaging.sms.yunpian.v1

import com.google.gson.Gson
import com.mobisist.components.messaging.Message
import com.mobisist.components.messaging.MessageSender
import com.mobisist.components.messaging.sms.SmsMessage
import com.mobisist.components.messaging.sms.SmsMessagingException
import com.mobisist.components.messaging.support.http.apache.post
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class YunPianConfig {
    lateinit var apikey: String
}

open class YunPianSender : MessageSender<SmsMessage, YunPianResponse> {

    protected val logger: Logger = LoggerFactory.getLogger(YunPianSender::class.java)

    lateinit var config: YunPianConfig

    override fun canHandle(msg: Message): Boolean = msg is SmsMessage

    @Throws(SmsMessagingException::class)
    override fun send(msg: SmsMessage): YunPianResponse {
        try {
            logger.debug("""prepare to send to ${msg.mobile}, with {
            |    text: ${msg.text}
            |}""".trimMargin())

            // 智能匹配模板发送
            val responseText = post("https://sms.yunpian.com/v1/sms/send.json", mapOf(
                    "apikey" to config.apikey,
                    "mobile" to msg.mobile,
                    "text" to msg.text
            ))
            logger.debug(responseText)

            val responseJson = Gson().fromJson(responseText, Map::class.java) as Map<String, Any>
            val response = YunPianResponse(responseJson)
            if (0 != response.code) {
                throw SmsMessagingException(response.msg)
            }

            return response

        } catch (e: SmsMessagingException) {
            throw e
        } catch (e: Exception) {
            throw SmsMessagingException(e)
        }
    }

}