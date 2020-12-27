package com.example.rpcdemo.register;

import com.example.rpcdemo.demo.nettydemo.kryoSerializerDemo.SPI;

import java.net.InetSocketAddress;

/**
 * @Author cxr
 * @Date 2020/12/19 13:12
 */
@SPI
public interface ServiceDiscovery {
    /**
     * 根据 rpcServiceName 获取远程服务地址
     *
     * @param rpcServiceName 完整的服务名称（class name+group+version）
     * @return 远程服务地址
     */
    InetSocketAddress lookupService(String rpcServiceName);
}
