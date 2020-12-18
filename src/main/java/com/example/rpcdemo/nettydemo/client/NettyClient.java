package com.example.rpcdemo.nettydemo.client;

import com.esotericsoftware.kryo.KryoSerializable;
import com.example.rpcdemo.nettydemo.dao.RpcRequest;
import com.example.rpcdemo.nettydemo.dao.RpcResponse;
import com.example.rpcdemo.nettydemo.encoderso.NettyKryoDecoder;
import com.example.rpcdemo.nettydemo.encoderso.NettyKryoEncoder;
import com.example.rpcdemo.nettydemo.handler.NettyClientHandler;
import com.example.rpcdemo.nettydemo.kryoSerializerDemo.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author aquarius_cxr
 * @Date 2020/12/17 17:55
 */
public class NettyClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private final String host;      // ip
    private final int port;         // 端口号
    private static final Bootstrap b;

    public NettyClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    static {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        b = new Bootstrap();
        KryoSerializer kryoSerializer = new KryoSerializer();
        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // 连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
                // 如果 15 秒之内没有发送数据给服务端的话，就发送一次心跳请求
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        /**
                         * 自定义序列化编解码器
                         */
                        // RpcResponse -> ByteBuf
                        socketChannel.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcResponse.class));
                        // ByteBuf -> RpcRequest
                        socketChannel.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcRequest.class));
                        socketChannel.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }

    public RpcResponse sendMessage(RpcRequest rpcRequest){
        try{
            ChannelFuture f = b.connect(host, port).sync();
            logger.info("client connect {}", host + ":" + port);
            Channel futureChannel = f.channel();
            logger.info("send message");
            if (futureChannel != null){
                futureChannel.writeAndFlush(rpcRequest).addListener(future -> {
                    if (future.isSuccess()){
                        logger.info("客户端发送消息: [{}]", rpcRequest.toString());
                    }else {
                        logger.error("发送失败:", future.cause());
                    }
                });
                // 阻塞等待，直到Channel关闭
                futureChannel.closeFuture().sync();
                // 将服务端返回的数据也就是RpcResponse对象取出
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                return futureChannel.attr(key).get();
            }
        } catch (InterruptedException e) {
            logger.error("连接服务端产生异常:", e);
        }
        return null;
    }
}
