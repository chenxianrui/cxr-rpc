package com.example.cxrrpc.demo.agent.sagent.proxy;

import com.example.cxrrpc.demo.agent.sagent.service.SmsService;

/**
 * @Author aquarius_cxr
 * @Date 2020/12/18 11:12
 */
public class SmsProxy implements SmsService {
    private final SmsService smsService;

    public SmsProxy(SmsService smsService){
        this.smsService = smsService;
    }

    @Override
    public String send(String message) {
        // 调用方法之前，我们可以添加自己的操作
        System.out.println("在调用方法 send() 之前");
        smsService.send(message);
        // 调用方法之后，我们同样可以添加自己的操作
        System.out.println("在调用方法 send() 之后");
        return null;
    }
}
