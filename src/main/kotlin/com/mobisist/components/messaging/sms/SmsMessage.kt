package com.mobisist.components.messaging.sms

import com.mobisist.components.messaging.Message

open class SmsMessage(val mobile: String, val text: String) : Message
