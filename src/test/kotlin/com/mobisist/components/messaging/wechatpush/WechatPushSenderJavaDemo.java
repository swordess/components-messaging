package com.mobisist.components.messaging.wechatpush;

import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;

import java.util.Properties;

import org.jetbrains.annotations.NotNull;

public class WechatPushSenderJavaDemo {

    public static void main(String[] args) {
        WechatPushSender sender = configViaWxMpConfigStorage();

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
        WechatPushMessage msg = new WechatPushMessage("userOpenId", "xweoi2342a114adf3284284");
        msg.setTo("first", new TemplateData("您已充值成功！", "#00ff00"));

        // 以下参数的颜色为默认的链接颜色: #173177
        msg.setTo("accountType", "VIP7");
        msg.setTo("account", "No.0001");

        // and: amount, result, remark

        // * 特别地, 若templateId需要从外部properties文件获取, 可通过TemplateIdProvider接口满足需求
        // 同时, 创建WechatPushMessage时给的参数是property key而不是template id
        sender.setTemplateIdProvider(new TemplateIdProvider() {

            // 定义外部配置文件, 对于Spring应用中的properties通过PropertySourcesPlaceholderConfigurer也可以完成
            private Properties externalProps = new Properties();

            public String get(@NotNull String templateKey) {
                return externalProps.get(templateKey).toString();
            }
        });

        try {
            sender.send(msg);
        } catch (WechatPushMessagingException e) {
            // you can handle exception here
        }
    }

    // 方式一、WxMpConfigStorage
    // 适合于仅需要微信推送的场景
    private static WechatPushSender configViaWxMpConfigStorage() {
        WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
        config.setAppId("your-appid");           // 设置微信公众号的appid
        config.setSecret("your-secret");         // 设置微信公众号的app corpSecret

        // these are optional, depends on your wx setting
        config.setToken("your-authtoken");       // 设置微信公众号的token
        config.setAesKey("your-EncodingAESKey"); // 设置微信公众号的EncodingAESKey

        WechatPushSender sender = new WechatPushSender();
        sender.setWxMpConfigStorage(config);
        return sender;
    }

    // 方式二、WxMpService
    // 适合于现有的微信二次开发系统, 复用应用原来的WxMpService以避免access_token刷新的问题
    private static WechatPushSender configViaWxMpService(WxMpService wxMpService) {
        WechatPushSender sender = new WechatPushSender();
        sender.setWxMpService(wxMpService);
        return sender;
    }

}
