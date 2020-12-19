package com.example.rpcdemo.remoting.transport.socket;


import com.example.rpcdemo.config.CustomShutdownHook;
import com.example.rpcdemo.factory.SingletonFactory;
import com.example.rpcdemo.provider.ServiceProvider;
import com.example.rpcdemo.provider.ServiceProviderImpl;
import com.example.rpcdemo.remoting.entity.RpcServiceProperties;
import com.example.rpcdemo.utils.concurrent.threadpool.ThreadPoolFactoryUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @Author cxr
 * @Date 2020/12/18 18:42
 *
 * 基于 socket 来做网络传输
 * socket 服务端
 */
@Slf4j
public class SocketRpcServer {

    private static final int PORT = 9998;
    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;

    public SocketRpcServer(){
        threadPool = ThreadPoolFactoryUtils.createCustomThreadPoolIfAbsent("socket 服务端 rpc 线程池");
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    public void registerService(Object service){
        serviceProvider.publishService(service);
    }

    public void registerService(Object service, RpcServiceProperties rpcServiceProperties){
        serviceProvider.publishService(service, rpcServiceProperties);
    }

    public void start(){
        try (ServerSocket server = new ServerSocket()){
            String host = InetAddress.getLocalHost().getHostAddress();
            server.bind(new InetSocketAddress(host, PORT));
            CustomShutdownHook.getCustomShutdownHook().clearAll();
            Socket socket;
            while ((socket = server.accept()) != null){
                log.info("客户端连接 [{}]", socket.getInetAddress());
                threadPool.execute(new SocketRpcRequestHandlerRunnable(socket));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            log.error("出现 IOException 异常:", e);
        }
    }
}
