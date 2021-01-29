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
        Object[] objects = {hello};    // 发送的数据（对象）
        Class<?>[] paramTypes = {Hello.class};      // 发送数据的类型
        NettyRpcClient nettyRpcClient = new NettyRpcClient();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.231.1",9998);
        nettyRpcClient.doConnect(inetSocketAddress);     // 连接目标服务
        nettyRpcClient.sendRpcRequest(new RpcRequest().builder()
                .methodName("hello")    // 需要调用de方法
                .parameters(objects)    // 发送的数据
                .paramTypes(paramTypes) // 发送的类型
                .requestId(UUID.randomUUID().toString())    // 随机一个唯一id
                .group("test2")         // 服务节点在 zookeeper 中的组
                .interfaceName("com.example.rpcdemo.hello.HelloServicetest2version2")   // 需要调用的接口名称
                .version("version2")    //版本号
                .build());
    }
}
