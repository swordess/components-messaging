package com.mobisist.components.messaging.sms

import com.mobisist.components.messaging.MessagingException

class SmsMessagingException : MessagingException {
    constructor(cause: Throwable) : super(cause)
    constructor(msg: String) : super(msg)
}