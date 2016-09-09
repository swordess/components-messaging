package com.mobisist.components.messaging.sms.yunpian.v1

import com.mobisist.components.messaging.sms.SmsMessage
import com.mobisist.components.messaging.sms.SmsMessagingException

fun main(args: Array<String>) {
    val sender = YunPianSender().apply {
        config = YunPianConfig().apply {
            apikey = "your-key"
        }
    }

    val msg = SmsMessage("13880808080", "Hello")

    try {
        val response = sender.send(msg)

        // if you care about the response, you may:

        // prints 0 if succeeded
        println(response.code)
        println(response.msg)

        // and you can retrieve any other info if you like
        val result = response.get("result") as Map<String, *>
        println(result["fee"])

        // more details see: https://www.yunpian.com/api/sms.html

    } catch (e: SmsMessagingException) {
        // you can handle exception here
    }
}
