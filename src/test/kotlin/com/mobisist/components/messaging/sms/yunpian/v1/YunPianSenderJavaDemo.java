package com.mobisist.components.messaging.sms.yunpian.v1;

import java.util.Map;

import com.mobisist.components.messaging.sms.SmsMessage;
import com.mobisist.components.messaging.sms.SmsMessagingException;

public class YunPianSenderJavaDemo {

    public static void main(String[] args) {
        YunPianConfig config = new YunPianConfig();
        config.setApikey("your-apiKey");

        YunPianSender sender = new YunPianSender();
        sender.setConfig(config);

        SmsMessage msg = new SmsMessage("13880808080", "Hello");

        try {
            YunPianResponse response = sender.send(msg);

            // if you care about the response, you may:

            // prints 0 if succeeded
            System.out.println(response.getCode());
            System.out.println(response.getMsg());

            // and you can retrieve any other info if you like
            Map<String, Object> result = (Map<String, Object>) response.get("result");
            System.out.println(result.get("fee"));

            // more details see: https://www.yunpian.com/api/sms.html

        } catch (SmsMessagingException e) {
            // you can handle exception here
        }
    }

}
