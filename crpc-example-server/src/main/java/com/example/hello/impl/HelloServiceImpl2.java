package com.example.hello.impl;

import com.example.hello.Hello;
import com.example.hello.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author cxr
 * @Date 2021/1/25 14:48
 */
@Slf4j
public class HelloServiceImpl2 implements HelloService {

    static {
        System.out.println("HelloServiceImpl2被创建");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl2收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl2返回: {}.", result);
        return result;
    }
}
