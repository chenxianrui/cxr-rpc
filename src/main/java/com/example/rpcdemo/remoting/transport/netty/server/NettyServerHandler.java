package com.example.rpcdemo.remoting.transport.netty.server;

import com.example.rpcdemo.factory.SingletonFactory;
import com.example.rpcdemo.remoting.handler.RpcRequestHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author cxr
 * @Date 2020/12/20 17:49
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final RpcRequestHandler rpcRequestHandler;

    public NettyServerHandler() {
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    /**
     * 读取从客户端消息，然后调用目标服务的目标方法并返回给客户端。
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 省略部分代码
    }


    // Netty 心跳机制相关。保证客户端和服务端的连接不被断掉，避免重连。
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 省略部分代码
    }

}
