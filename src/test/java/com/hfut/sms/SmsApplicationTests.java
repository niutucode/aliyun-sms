package com.hfut.sms;

import com.hfut.sms.component.AliyunSmsComponent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class SmsApplicationTests {

    @Resource
    AliyunSmsComponent aliyunSmsComponent;

    @Test
    void contextLoads() {
    }

    @Test
    void testAliyunSms() {
        // 输入手机号码
        String phoneNumber = "xxxxxxxxxxx";
        Boolean result = aliyunSmsComponent.obtainAuthCode(phoneNumber);
        System.out.println(result ? "发送成功！" : "发送失败！");
        // 获取Redis中存储的验证码
        String authCode = aliyunSmsComponent.redisTemplate.opsForValue().get(phoneNumber);
        System.out.println("验证码为：" + authCode);
    }
}
