package com.example.cxrrpc.demo.nettydemo.server;

import com.example.cxrrpc.demo.nettydemo.dao.RpcRequest;
import com.example.cxrrpc.demo.nettydemo.dao.RpcResponse;
import com.example.cxrrpc.demo.nettydemo.encoderso.NettyKryoDecoder;
import com.example.cxrrpc.demo.nettydemo.encoderso.NettyKryoEncoder;
import com.example.cxrrpc.demo.nettydemo.handler.NettyServerHandler;
import com.example.cxrrpc.demo.nettydemo.kryoSerializerDemo.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @Author aquarius_cxr
 * @Date 2020/12/17 21:29
 */
public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private final int port;

    public NettyServer(int port){
        this.port = port;
    }

    public void run(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        KryoSerializer kryoSerializer = new KryoSerializer();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据块，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
            .childOption(ChannelOption.TCP_NODELAY, true)
                    // 是否开启 TCP 底层心跳机制
            .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 标识系统用于临时存放已完成三次握手的请求的队列的最大长度，如果将连接简历频繁，服务器处理创建新连接较慢，可以适当调大这个参数
            .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcRequest.class));
                            socketChannel.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcResponse.class));
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            // 绑定端口，同步等待绑定成功
            ChannelFuture f = b.bind(port).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务时发生错误:", e);
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
