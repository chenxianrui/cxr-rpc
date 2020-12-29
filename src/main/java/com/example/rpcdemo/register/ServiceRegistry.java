package com.example.rpcdemo.register;

import java.net.InetSocketAddress;

/**
 * @Author cxr
 * @Date 2020/12/19 20:24
 *
 * 服务注册
 */
public interface ServiceRegistry {

    /**
     * 注册服务
     *
     * @param rpcServiceName        完整的服务名称（class name+group+version）
     * @param inetSocketAddress     远程服务地址
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
