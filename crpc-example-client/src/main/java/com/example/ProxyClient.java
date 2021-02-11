package com.example;


import com.example.hello.Hello;
import com.example.hello.HelloService;
import com.example.proxy.RpcClientProxy;
import com.example.remoting.entity.RpcServiceProperties;
import com.example.remoting.transport.netty.client.NettyClientTransport;

/**
 * @Author cxr
 * @Date 2021/1/28 8:57
 */
public class ProxyClient {
    public static void main(String[] args) {
        NettyClientTransport nettyClientTransport = new NettyClientTransport();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test2").version("version2").build();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(nettyClientTransport, rpcServiceProperties);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("调用", " 成功"));
        System.out.println(hello);
    }
}
