package com.example.rpcdemo.testmain.netty;

import com.example.rpcdemo.remoting.transport.netty.server.NettyRpcServer;

/**
 * @Author cxr
 * @Date 2020/12/29 20:35
 */
public class ServerMain {
    public static void main(String[] args) {
        NettyRpcServer nettyRpcServer = new NettyRpcServer();
        nettyRpcServer.start();
    }
}
