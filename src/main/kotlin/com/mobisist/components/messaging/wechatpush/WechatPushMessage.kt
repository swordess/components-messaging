package com.mobisist.components.messaging.wechatpush

import com.mobisist.components.messaging.Message
import com.mobisist.components.messaging.support.common.DATE_FORMAT_LONG
import java.text.DateFormat
import java.util.*

sealed class DataValue(var value: String?) {

    class DateValue @JvmOverloads constructor(
            date: Date?,
            format: DateFormat = DATE_FORMAT_LONG.get()) : DataValue(if (date != null) { format.format(date) } else null) {
    }

}

data class TemplateData @JvmOverloads constructor(var value: String?, var color: String? = "#173177") {
    @JvmOverloads constructor(dateValue: DataValue.DateValue, color: String? = "#173177") : this(dateValue.value, color)
}

interface TemplateIdProvider {
    fun get(templateKey: String): String
}

open class WechatPushMessage @JvmOverloads constructor(val openId: String, val templateKey: String, var url: String = "") : Message {
    val templateData = mutableMapOf<String, TemplateData>()

    infix fun String.setTo(dataValue: String) {
        templateData.put(this, TemplateData(dataValue))
    }

    infix fun String.setTo(data: TemplateData) {
        templateData.put(this, data)
    }
}