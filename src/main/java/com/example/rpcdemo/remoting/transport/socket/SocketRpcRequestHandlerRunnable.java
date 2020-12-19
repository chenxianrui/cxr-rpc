package com.example.rpcdemo.remoting.transport.socket;

import com.example.rpcdemo.factory.SingletonFactory;
import com.example.rpcdemo.remoting.dto.RpcRequest;
import com.example.rpcdemo.remoting.dto.RpcResponse;
import com.example.rpcdemo.remoting.handler.RpcRequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @Author cxr
 * @Date 2020/12/18 18:41
 *
 * 基于 socket 来做网络传输
 * 处理请求
 */
@Slf4j
public class SocketRpcRequestHandlerRunnable implements Runnable{

    private final Socket socket;
    private final RpcRequestHandler rpcRequestHandler;

    public SocketRpcRequestHandlerRunnable(Socket socket){
        this.socket = socket;
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }


    @Override
    public void run() {
        log.info("服务端从客户端获取信息通过线程处理：[{}]", Thread.currentThread().getName());
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())){
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Object result = rpcRequestHandler.handle(rpcRequest);
            objectOutputStream.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
            objectOutputStream.flush();
        }catch (IOException | ClassNotFoundException e) {
            log.error("出现异常:", e);
        }
    }
}
