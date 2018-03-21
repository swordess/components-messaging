package com.mobisist.components.messaging.sms

import com.mobisist.components.messaging.MessagingException

class SmsMessagingException : MessagingException {

    constructor(cause: Throwable, detail: String) : super(cause) {
        this.code = null
        this.detail = detail
    }

    constructor(code: Int, msg: String, detail: String) : super(msg) {
        this.code = code
        this.detail = detail
    }

    val code: Int?
    val detail: String

    override fun toString(): String {
        val result = super.toString()
        return "$result, code=$code, detail=$detail"
    }

}