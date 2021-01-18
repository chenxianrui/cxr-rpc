package com.example.rpcdemo.remoting.transport.netty.client;

import com.example.rpcdemo.demo.nettydemo.handler.NettyClientHandler;
import com.example.rpcdemo.enums.CompressTypeEnum;
import com.example.rpcdemo.enums.SerializationTypeEnum;
import com.example.rpcdemo.registry.ServiceDiscovery;
import com.example.rpcdemo.remoting.constants.RpcConstants;
import com.example.rpcdemo.remoting.dto.RpcMessage;
import com.example.rpcdemo.remoting.dto.RpcRequest;
import com.example.rpcdemo.remoting.dto.RpcResponse;
import com.example.rpcdemo.remoting.transport.RpcRequestTransport;
import com.example.rpcdemo.remoting.transport.netty.codec.RpcMessageDecoder;
import com.example.rpcdemo.remoting.transport.netty.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Author cxr
 * @Date 2020/12/20 17:48
 */
@Slf4j
public final class NettyRpcClient implements RpcRequestTransport{

    private final ServiceDiscovery serviceDiscovery = null;
    private final UnprocessedRequests unprocessedRequests = null;
    private final ChannelProvider channelProvider = null;
    private final Bootstrap bootstrap;
//    private final EventLoopGroup eventExecutors = null;
    private final EventLoopGroup eventLoopGroup;

    public NettyRpcClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                //  The timeout period of the connection.
                //  If this time is exceeded or the connection cannot be established, the connection fails.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        // If no data is sent to the server within 15 seconds, a heartbeat request is sent
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(new NettyClientHandler());
                    }
                });
    }

    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress){
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()){
                log.info("连接客户端 [{}] 成功", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            }else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }


    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        // 构造返回值
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        // 通过 rpcRequest 构造rpc服务名称
        String rpcServiceName = rpcRequest.toRpcProperties().toRpcServiceName();
        // 获取服务地址
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcServiceName);
        // 获取服务地址相关的channel
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel.isActive()){
            // 存储未处理请求
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            RpcMessage rpcMessage = new RpcMessage();
            rpcMessage.setData(rpcRequest);
            rpcMessage.setCodec(SerializationTypeEnum.KYRO.getCode());
            rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
            rpcMessage.setMessageType(RpcConstants.REQUEST_TYPE);
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("客户端发送信息: [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("发送失败:", future.cause());
                }
            });
        }else {
            throw new IllegalStateException();
        }
        return resultFuture;
    }
}
