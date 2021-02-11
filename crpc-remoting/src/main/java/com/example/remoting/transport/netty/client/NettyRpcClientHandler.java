package com.example.remoting.transport.netty.client;

import com.example.enums.CompressTypeEnum;
import com.example.enums.SerializationTypeEnum;
import com.example.factory.SingletonFactory;
import com.example.remoting.constants.RpcConstants;
import com.example.remoting.dto.RpcMessage;
import com.example.remoting.dto.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @Author cxr
 * @Date 2020/12/20 17:48
 */
@Slf4j
public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter{
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;

    public NettyRpcClientHandler(){
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    /**
     * 读取从服务端返回的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        try {
            log.info("客户端收到消息：[{}]", msg);
            if (msg instanceof RpcResponse){
                RpcResponse<Object> rpcResponse = (RpcResponse<Object>) msg;
                unprocessedRequests.complete(rpcResponse);
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    // Netty 心跳机制。保证客户端和服务端的连接不被断掉，避免重连。
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("write idle 发生 [{}]", ctx.channel().remoteAddress());
                Channel channel = channelProvider.get((InetSocketAddress) ctx.channel().remoteAddress());
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationTypeEnum.KYRO.getCode());
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                rpcMessage.setMessageType(RpcConstants.HEARTBEAT_REQUEST_TYPE);
                rpcMessage.setData(RpcConstants.PING);
                channel.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
