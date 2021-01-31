package com.example.cxrrpc.demo.nettydemo.handler;

import com.example.cxrrpc.factory.SingletonFactory;
import com.example.cxrrpc.remoting.constants.RpcConstants;
import com.example.cxrrpc.remoting.dto.RpcMessage;
import com.example.cxrrpc.remoting.dto.RpcResponse;
import com.example.cxrrpc.remoting.transport.netty.client.UnprocessedRequests;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author aquarius_cxr
 * @Date 2020/12/17 20:30
 *
 * 自定义 channelHandler 处理服务端消息
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);
    private final UnprocessedRequests unprocessedRequests;

    public NettyClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        try {
            logger.info("客户端收到消息: [{}]", msg);
            if (msg instanceof RpcMessage) {
                RpcMessage tmp = (RpcMessage) msg;
                byte messageType = tmp.getMessageType();
                if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                    logger.info("获取到消息 [{}]", tmp.getData());
                } else if (messageType == RpcConstants.RESPONSE_TYPE) {
                    RpcResponse<Object> rpcResponse = (RpcResponse<Object>) tmp.getData();
                    unprocessedRequests.complete(rpcResponse);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.error("客户端捕捉到异常", cause);
        ctx.close();
    }
}
