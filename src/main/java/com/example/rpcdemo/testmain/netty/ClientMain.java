package com.example.rpcdemo.testmain.netty;

import com.example.rpcdemo.remoting.dto.RpcRequest;
import com.example.rpcdemo.remoting.transport.netty.client.NettyRpcClient;

import java.net.InetSocketAddress;

/**
 * @Author cxr
 * @Date 2020/12/29 20:35
 */
public class ClientMain {
    public static void main(String[] args) {
        NettyRpcClient nettyRpcClient = new NettyRpcClient();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(9998);
        nettyRpcClient.doConnect(inetSocketAddress);
        nettyRpcClient.sendRpcRequest(new RpcRequest().builder().requestId("1").group("test").interfaceName("inter").version("1.0").build());
    }
}
