package com.mobisist.components.messaging.support.http.apache

import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

internal fun post(url: String, params: Map<String, String>?): String {
    HttpClients.createDefault().use {
        val method = HttpPost(url).apply {
            if (params != null) {
                entity = UrlEncodedFormEntity(params.map {
                    BasicNameValuePair(it.key, it.value)
                }, "UTF-8")
            }
        }

        it.execute(method).use { resp ->
            if (resp.entity != null) {
                return EntityUtils.toString(resp.entity)
            }
        }
    }

    return ""
}
