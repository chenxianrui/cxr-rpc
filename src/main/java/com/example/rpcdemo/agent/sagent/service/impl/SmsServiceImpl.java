package com.example.rpcdemo.agent.sagent.service.impl;

import com.example.rpcdemo.agent.sagent.service.SmsService;

/**
 * @Author aquarius_cxr
 * @Date 2020/12/18 11:11
 */
public class SmsServiceImpl implements SmsService {
    @Override
    public String send(String message) {
        System.out.println("发送消息："+message);
        return message;
    }
}
