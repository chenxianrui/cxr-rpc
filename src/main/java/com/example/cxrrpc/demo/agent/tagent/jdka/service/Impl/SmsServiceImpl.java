package com.example.cxrrpc.demo.agent.tagent.jdka.service.Impl;

import com.example.cxrrpc.demo.agent.tagent.jdka.service.SmsService;

/**
 * @Author cxr
 * @Date 2020/12/18 11:27
 */
public class SmsServiceImpl implements SmsService {
    public String send(String message) {
        System.out.println("发送消息:" + message);
        return message;
    }
}
