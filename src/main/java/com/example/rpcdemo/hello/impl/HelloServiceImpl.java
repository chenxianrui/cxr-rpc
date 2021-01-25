package com.example.rpcdemo.hello.impl;

import com.example.rpcdemo.hello.Hello;
import com.example.rpcdemo.hello.HelloService;
import com.example.rpcdemo.hello.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author cxr
 * @Date 2021/1/25 14:47
 */
@Slf4j
@RpcService(group = "test1", version = "version1")
public class HelloServiceImpl implements HelloService {

    static {
        System.out.println("HelloServiceImpl被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl返回: {}.", result);
        return result;
    }
}
