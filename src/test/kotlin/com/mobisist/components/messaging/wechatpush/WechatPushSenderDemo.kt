package com.mobisist.components.messaging.wechatpush

import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage
import me.chanjar.weixin.mp.api.WxMpService
import java.util.*

fun main(args: Array<String>) {
    // 提供以下两种配置方式, 此处示例使用方式一
    val sender = configViaWxMpConfigStorage()

    // 假设在微信公众号内有以下模板消息, 其模板消息id为: xweoi2342a114adf3284284
    // 内容为:
    /*
      标题: 充值通知
      详细内容:
          {{first.DATA}}

          {{accountType.DATA}}：{{account.DATA}}
          充值金额：{{amount.DATA}}
          充值状态：{{result.DATA}}
          {{remark.DATA}}
    */

    // 则可以创建以下模板消息对象
    val msg = WechatPushMessage("userOpenId", "xweoi2342a114adf3284284").apply {
        "first" setTo TemplateData("您已充值成功！", color = "#00ff00")

        // 以下参数的颜色为默认的链接颜色: #173177
        "accountType" setTo "VIP7"
        "account" setTo "No.0001"

        // and: amount, result, remark
    }

    // * 特别地, 若templateId需要从外部properties文件获取, 可通过TemplateIdProvider接口满足需求
    // 同时, 创建WechatPushMessage时给的参数是property key而不是template id
    sender.templateIdProvider = object : TemplateIdProvider {

        // 定义外部配置文件, 对于Spring应用中的properties通过PropertySourcesPlaceholderConfigurer也可以完成
        private val externalProps = Properties()

        override fun get(templateKey: String): String {
            return externalProps[templateKey].toString()
        }
    }

    try {
        sender.send(msg)
    } catch (e: WechatPushMessagingException) {
        // you can handle exception here
    }
}

// 方式一、WxMpConfigStorage
// 适合于仅需要微信推送的场景
private fun configViaWxMpConfigStorage(): WechatPushSender {
    val config = WxMpInMemoryConfigStorage().apply {
        appId = "your-appid" // 设置微信公众号的appid
        secret = "your-secret" // 设置微信公众号的app corpSecret

        // these are optional, depends on your wx setting
        token = "your-authtoken" // 设置微信公众号的token
        aesKey = "your-EncodingAESKey" // 设置微信公众号的EncodingAESKey
    }

    return WechatPushSender().apply {
        wxMpConfigStorage = config
    }
}

// 方式二、WxMpService
// 适合于现有的微信二次开发系统, 复用应用原来的WxMpService以避免access_token刷新的问题
private fun configViaWxMpService(wxMpService: WxMpService): WechatPushSender {
    return WechatPushSender().apply {
        this.wxMpService = wxMpService
    }
}