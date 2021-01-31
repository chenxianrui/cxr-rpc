package com.example.cxrrpc.remoting.transport.netty.client;

import com.example.cxrrpc.enums.CompressTypeEnum;
import com.example.cxrrpc.enums.SerializationTypeEnum;
import com.example.cxrrpc.extension.ExtensionLoader;
import com.example.cxrrpc.factory.SingletonFactory;
import com.example.cxrrpc.registry.ServiceDiscovery;
import com.example.cxrrpc.remoting.constants.RpcConstants;
import com.example.cxrrpc.remoting.dto.RpcMessage;
import com.example.cxrrpc.remoting.dto.RpcRequest;
import com.example.cxrrpc.remoting.dto.RpcResponse;
import com.example.cxrrpc.remoting.transport.RpcRequestTransport;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @Author cxr
 * @Date 2020/12/29 15:50
 *
 * 基于netty的传输rpcRequest
 */
@Slf4j
public class NettyClientTransport implements RpcRequestTransport {
    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;

    public NettyClientTransport() {
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    @Override
    public CompletableFuture<RpcResponse<Object>> sendRpcRequest(RpcRequest rpcRequest) {
        // 构造返回值
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        // 通过 rpcRequest 构造rpc服务名
        String rpcServiceName = rpcRequest.toRpcProperties().toRpcServiceName();
        // 获取服务地址
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcServiceName);
        // 获取服务器地址相关的通道
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel != null && channel.isActive()) {
            // 保存未处理的请求
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            RpcMessage rpcMessage = new RpcMessage();
            rpcMessage.setData(rpcRequest);
            rpcMessage.setCodec(SerializationTypeEnum.KYRO.getCode());
            rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
            rpcMessage.setMessageType(RpcConstants.REQUEST_TYPE);
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("客户端发送消息: [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("发送失败:", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }

        return resultFuture;
    }

}
