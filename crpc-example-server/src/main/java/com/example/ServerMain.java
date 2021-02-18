package com.example;

import com.example.hello.HelloService;
import com.example.hello.impl.HelloServiceImpl2;
import com.example.remoting.entity.RpcServiceProperties;
import com.example.remoting.transport.netty.server.NettyRpcServer;

/**
 * @Author cxr
 * @Date 2020/12/29 20:35
 */
public class ServerMain {
    public static void main(String[] args) {
//        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ServerMain.class);
//        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        NettyRpcServer nettyRpcServer = new NettyRpcServer();
        HelloService helloService2 = new HelloServiceImpl2();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test3").version("version3").build();
        nettyRpcServer.registerService(helloService2, rpcServiceProperties);    // 服务端向 zookeeper 注册服务
        nettyRpcServer.start();
    }
}
