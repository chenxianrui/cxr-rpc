package com.example.rpcdemo.proxy;

import com.example.rpcdemo.enums.RpcErrorMessageEnum;
import com.example.rpcdemo.enums.RpcResponseCodeEnum;
import com.example.rpcdemo.exception.RpcException;
import com.example.rpcdemo.remoting.dto.RpcRequest;
import com.example.rpcdemo.remoting.dto.RpcResponse;
import com.example.rpcdemo.remoting.entity.RpcServiceProperties;
import com.example.rpcdemo.remoting.transport.RpcRequestTransport;
import com.example.rpcdemo.remoting.transport.netty.client.NettyClientTransport;
import com.example.rpcdemo.remoting.transport.socket.SocketRpcClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @Author cxr
 * @Date 2020/12/29 15:26
 *
 * 动态代理类
 * 当动态代理对象调用一个方法时，它实际上调用下面的invoke方法。
 * 正是由于动态代理，客户端调用的远程方法就像调用本地方法一样(中间过程是屏蔽的)
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private static final String INTERFACE_NAME = "interfaceName";

    /**
     * 用于向服务器发送请求。有两种实现:socket和netty
     */
    private RpcRequestTransport clientTransport;
    private RpcServiceProperties rpcServiceProperties;

    public RpcClientProxy(RpcRequestTransport clientTransport, RpcServiceProperties rpcServiceProperties){
        this.clientTransport = clientTransport;
        if (rpcServiceProperties.getGroup() == null){
            rpcServiceProperties.setGroup("");
        }
        if (rpcServiceProperties.getVersion() == null){
            rpcServiceProperties.setVersion("");
        }
        this.rpcServiceProperties = rpcServiceProperties;
    }

    public RpcClientProxy(RpcRequestTransport clientTransport){
        this.clientTransport = clientTransport;
        this.rpcServiceProperties = RpcServiceProperties.builder().group("").version("").build();
    }

    /**
     * 获取代理类
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        log.info("调用方法: [{}]", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceProperties.getGroup())
                .version(rpcServiceProperties.getVersion())
                .build();
        RpcResponse<Object> rpcResponse = null;
        if (clientTransport instanceof NettyClientTransport) {
            CompletableFuture<RpcResponse<Object>> completableFuture = (CompletableFuture<RpcResponse<Object>>) clientTransport.sendRpcRequest(rpcRequest);
            rpcResponse = completableFuture.get();
        }
        if (clientTransport instanceof SocketRpcClient) {
            rpcResponse = (RpcResponse<Object>) clientTransport.sendRpcRequest(rpcRequest);
        }
        this.check(rpcResponse, rpcRequest);
        return rpcResponse.getData();
    }

    private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCodeEnum.SUCCESS.getCode())) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
