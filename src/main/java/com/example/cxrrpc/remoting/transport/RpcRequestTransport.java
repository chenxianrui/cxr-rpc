package com.example.cxrrpc.remoting.transport;

import com.example.cxrrpc.demo.nettydemo.kryoSerializerDemo.SPI;
import com.example.cxrrpc.remoting.dto.RpcRequest;

/**
 * @Author cxr
 * @Date 2020/12/18 18:40
 *
 * 消息传输接口
 */
@SPI
public interface RpcRequestTransport {

    /**
     * 发送 rpc 请求到服务端并获取返回结果
     *
     * @param rpcRequest 消息体
     * @return 从服务端返回数据
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
