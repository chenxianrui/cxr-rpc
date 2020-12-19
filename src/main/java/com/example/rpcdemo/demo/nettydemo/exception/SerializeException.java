package com.example.rpcdemo.demo.nettydemo.exception;

/**
 * @Author aquarius_cxr
 * @Date 2020/12/17 18:42
 * 自定义序列化异常类
 */
public class SerializeException extends RuntimeException {
    public SerializeException(String message){
        super(message);
    }
}
