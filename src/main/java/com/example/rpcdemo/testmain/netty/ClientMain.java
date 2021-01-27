package com.example.rpcdemo.testmain.netty;

import com.example.rpcdemo.hello.Hello;
import com.example.rpcdemo.hello.HelloService;
import com.example.rpcdemo.remoting.dto.RpcRequest;
import com.example.rpcdemo.remoting.entity.RpcServiceProperties;
import com.example.rpcdemo.remoting.transport.netty.client.NettyRpcClient;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * @Author cxr
 * @Date 2020/12/29 20:35
 */
public class ClientMain {
    public static void main(String[] args) {
        Hello hello = new Hello();
        hello.setDescription("成功调用");
        hello.setMessage("nice");
        Object[] objects = {hello};
        Class<?>[] paramTypes = {Hello.class};
        RpcServiceProperties rpcServiceProperties = new RpcServiceProperties();
        NettyRpcClient nettyRpcClient = new NettyRpcClient();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.231.1",9998);
        nettyRpcClient.doConnect(inetSocketAddress);
        nettyRpcClient.sendRpcRequest(new RpcRequest().builder()
                .methodName("hello")
                .parameters(objects)
                .paramTypes(paramTypes)
                .requestId(UUID.randomUUID().toString())
                .group("test2")
                .interfaceName("com.example.rpcdemo.hello.HelloServicetest2version2")
                .version("version2")
                .build());
    }
}
