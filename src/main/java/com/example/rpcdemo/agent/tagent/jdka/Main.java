package com.example.rpcdemo.agent.tagent.jdka;

import com.example.rpcdemo.agent.tagent.jdka.proxy.JdkProxyFactory;
import com.example.rpcdemo.agent.tagent.jdka.service.Impl.SmsServiceImpl;
import com.example.rpcdemo.agent.tagent.jdka.service.SmsService;

/**
 * @Author cxr
 * @Date 2020/12/18 11:42
 */
public class Main {
    public static void main(String[] args) {
        SmsService smsService = new SmsServiceImpl();
        SmsService smsServiceImplProxy = (SmsService) JdkProxyFactory.getProxy(smsService);
        smsServiceImplProxy.send("java");
    }
}
