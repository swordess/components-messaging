package com.mobisist.components.messaging.support.common

import java.text.DateFormat
import java.text.SimpleDateFormat

internal val DATE_FORMAT_LONG = object : ThreadLocal<DateFormat>() {
    override fun initialValue() = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
}
