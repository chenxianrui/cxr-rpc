package com.example.rpcdemo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author cxr
 * @Date 2020/12/19 12:25
 */
@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseCodeEnum {

    SUCCESS(200, "远程传输响应成功"),
    FAIL(500, "远程传输响应失败");
    private int code;

    private final String message;
}
