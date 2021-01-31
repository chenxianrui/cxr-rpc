package com.example.cxrrpc.demo.nettydemo.testmain;

import com.example.cxrrpc.demo.nettydemo.client.NettyClient;
import com.example.cxrrpc.demo.nettydemo.dao.RpcRequest;
import com.example.cxrrpc.demo.nettydemo.dao.RpcResponse;

/**
 * @Author aquarius_cxr
 * @Date 2020/12/17 21:41
 */
public class ClientMain {
    public static void main(String[] args) {
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName("interface")
                .methodName("hello").build();
        NettyClient nettyClient = new NettyClient("127.0.0.1", 8889);
        for (int i=0; i<3; i++){
            nettyClient.sendMessage(rpcRequest);
        }
        RpcResponse rpcResponse = nettyClient.sendMessage(rpcRequest);
        System.out.println(rpcResponse.toString());
    }
}
