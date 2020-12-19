package com.example.rpcdemo.testmain.socket;

import com.example.rpcdemo.remoting.dto.RpcRequest;
import com.example.rpcdemo.remoting.transport.socket.SocketRpcClient;

/**
 * @Author cxr
 * @Date 2020/12/19 20:55
 */
public class Client {
    public static void main(String[] args) {
        RpcRequest rpcRequest = new RpcRequest();
        new SocketRpcClient().sendRpcRequest(rpcRequest);
    }
}
