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
     * 寻找服务
     *
     * @param rpcServiceName rpc 服务端名称
     * @return 服务端地址
     */
    InetSocketAddress lookupService(String rpcServiceName);
}
