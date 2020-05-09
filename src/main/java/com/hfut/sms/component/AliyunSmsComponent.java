package com.hfut.sms.component;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author Chenzh
 */
@Component
public class AliyunSmsComponent {


    final String ACCESS_ID = "xxxxxxxxxxx";
    final String ACCESS_SECRET = "xxxxxxxxxxx";
    /**
     * API错误码
     */
    final String RESPONSE_CODE = "OK";

    @Resource
    public RedisTemplate<String, String> redisTemplate;

    /**
     * 获取验证码
     *
     * @param phoneNumber 手机号码
     * @return 验证码是否获取成功
     */
    public Boolean obtainAuthCode(String phoneNumber) {
        DefaultProfile profile = DefaultProfile.getProfile(
                "cn-hangzhou", ACCESS_ID, ACCESS_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        // 传入手机号码
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        // 输入签名
        request.putQueryParameter("SignName", "xxxxxxxxxxx");
        // 输入模板编号
        request.putQueryParameter("TemplateCode", "xxxxxxxxxxx");
        // 产生6位数随机码
        String authCode = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
        request.putQueryParameter("TemplateParam", "{code: " + authCode + "}");
        try {
            // 返回响应的JSON数据
            CommonResponse response = client.getCommonResponse(request);
            JSONObject responseJson = JSONObject.parseObject(response.getData());
            String responseCode = responseJson.getString("Code");
            // 根据API错误码判断是否发送成功
            if (RESPONSE_CODE.equals(responseCode)) {
                // 存入Redis，有效期为5分钟
                redisTemplate.opsForValue().set(phoneNumber, authCode, 5, TimeUnit.MINUTES);
                return true;
            } else {
                return false;
            }
        } catch (ClientException e) {
            e.printStackTrace();
            return false;
        }
    }
}
