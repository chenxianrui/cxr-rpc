package com.example.cxrrpc.testmain.netty;

import com.example.cxrrpc.hello.Hello;
import com.example.cxrrpc.hello.HelloService;
import com.example.cxrrpc.proxy.RpcClientProxy;
import com.example.cxrrpc.remoting.entity.RpcServiceProperties;
import com.example.cxrrpc.remoting.transport.netty.client.NettyClientTransport;
import com.example.cxrrpc.remoting.transport.netty.client.NettyRpcClient;

import java.net.InetSocketAddress;

/**
 * @Author cxr
 * @Date 2021/1/28 8:57
 */
public class ProxyClient {
    public static void main(String[] args) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.231.1",9998);
        NettyRpcClient nettyRpcClient = new NettyRpcClient();
        nettyRpcClient.doConnect(inetSocketAddress);
        NettyClientTransport nettyClientTransport = new NettyClientTransport();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test2").version("version2").build();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(nettyClientTransport, rpcServiceProperties);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("调用", " 成功"));
        System.out.println(hello);
    }
}
