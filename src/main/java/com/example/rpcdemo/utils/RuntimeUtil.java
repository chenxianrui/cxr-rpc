package com.example.rpcdemo.utils;

/**
 * @Author cxr
 * @Date 2020/12/21 13:16
 */
public class RuntimeUtil {
    /**
     * 获取CPU的核心数
     * @return cpu的核心数
     */
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}
