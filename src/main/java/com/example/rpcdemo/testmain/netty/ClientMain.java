package com.example.rpcdemo.testmain.netty;

import com.example.rpcdemo.remoting.transport.netty.client.NettyRpcClient;

import java.net.InetSocketAddress;

/**
 * @Author cxr
 * @Date 2020/12/29 20:35
 */
public class ClientMain {
    public static void main(String[] args) {
        NettyRpcClient nettyRpcClient = new NettyRpcClient();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost",9998);
        nettyRpcClient.doConnect(inetSocketAddress);
    }
}
