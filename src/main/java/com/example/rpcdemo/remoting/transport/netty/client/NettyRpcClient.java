package com.example.rpcdemo.remoting.transport.netty.client;

import com.example.rpcdemo.enums.CompressTypeEnum;
import com.example.rpcdemo.enums.SerializationTypeEnum;
import com.example.rpcdemo.register.ServiceDiscovery;
import com.example.rpcdemo.remoting.constants.RpcConstants;
import com.example.rpcdemo.remoting.dto.RpcMessage;
import com.example.rpcdemo.remoting.dto.RpcRequest;
import com.example.rpcdemo.remoting.dto.RpcResponse;
import com.example.rpcdemo.remoting.transport.RpcRequestTransport;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @Author cxr
 * @Date 2020/12/20 17:48
 */
@Slf4j
public final class NettyRpcClient implements RpcRequestTransport{

    private final ServiceDiscovery serviceDiscovery = null;
    private final UnprocessedRequests unprocessedRequests = null;
    private final ChannelProvider channelProvider = null;
    private final Bootstrap bootstrap = null;
    private final EventLoopGroup eventExecutors = null;

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
