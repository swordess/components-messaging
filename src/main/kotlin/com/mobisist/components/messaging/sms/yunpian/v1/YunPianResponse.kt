package com.mobisist.components.messaging.sms.yunpian.v1

class YunPianResponse(private val map: Map<String, Any?>) {

    val code: Int
        get() = map["code"].toString().toDouble().toInt() // returned code might be "0.0"

    val msg: String
        get() = map["msg"].toString()

    val detail: String
        get() = map["detail"]?.toString() ?: ""

    fun get(key: String): Any? = map[key]

    override fun toString() = map.toString()

}