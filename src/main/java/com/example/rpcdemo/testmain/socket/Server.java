package com.example.rpcdemo.testmain.socket;

import com.example.rpcdemo.remoting.transport.socket.SocketRpcServer;

/**
 * @Author cxr
 * @Date 2020/12/19 20:55
 */
public class Server {
    public static void main(String[] args) {
        new SocketRpcServer().start();
    }
}
