package com.example.cxrrpc.remoting.transport.socket;

import com.example.cxrrpc.exception.RpcException;
import com.example.cxrrpc.extension.ExtensionLoader;
import com.example.cxrrpc.registry.ServiceDiscovery;
import com.example.cxrrpc.remoting.dto.RpcRequest;
import com.example.cxrrpc.remoting.entity.RpcServiceProperties;
import com.example.cxrrpc.remoting.transport.RpcRequestTransport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Author cxr
 * @Date 2020/12/18 18:40
 *
 * 基于 socket 来做网络传输
 * socket 客户端
 */
@AllArgsConstructor
@Slf4j
public class SocketRpcClient implements RpcRequestTransport{
    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient(){
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
    }


    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        // 通过 rpcRequest 创建rpc服务名称
        String rpcServiceName = RpcServiceProperties.builder()
                .serviceName(rpcRequest.getInterfaceName())
                .group(rpcRequest.getGroup())
                .version(rpcRequest.getVersion())
                .build().toRpcServiceName();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcServiceName);
        try (Socket socket = new Socket()){
            socket.connect(inetSocketAddress);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            // 通过输出流将数据发送到服务端
            objectOutputStream.writeObject(rpcRequest);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // 从输入流中读取 RpcResponse
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException("调用服务失败", e);
        }
    }
}
