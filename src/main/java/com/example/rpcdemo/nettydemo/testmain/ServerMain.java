package com.example.rpcdemo.nettydemo.testmain;

import com.example.rpcdemo.nettydemo.server.NettyServer;

/**
 * @Author aquarius_cxr
 * @Date 2020/12/17 21:42
 */
public class ServerMain {
    public static void main(String[] args) {
        new NettyServer(8889).run();
    }
}
