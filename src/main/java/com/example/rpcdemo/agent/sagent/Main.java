package com.example.rpcdemo.agent.sagent;

import com.example.rpcdemo.agent.sagent.proxy.SmsProxy;
import com.example.rpcdemo.agent.sagent.service.SmsService;
import com.example.rpcdemo.agent.sagent.service.impl.SmsServiceImpl;

/**
 * @Author aquarius_cxr
 * @Date 2020/12/18 11:15
 */
public class Main {
    public static void main(String[] args) {
        SmsService smsService = new SmsServiceImpl();
        SmsProxy smsProxy = new SmsProxy(smsService);
        smsProxy.send("java");
    }
}
