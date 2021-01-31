package com.example.cxrrpc.provider;

import com.example.cxrrpc.remoting.entity.RpcServiceProperties;

/**
 * @Author cxr
 * @Date 2020/12/19 13:55
 */
public interface ServiceProvider {

    /**
     * @param service               服务类型
     * @param serviceClass          由服务实例对象实现的接口类
     * @param rpcServiceProperties  服务实例相关的属性
     */
    void addService(Object service, Class<?> serviceClass, RpcServiceProperties rpcServiceProperties);

    /**
     *
     * @param rpcServiceProperties  服务实例相关的属性
     * @return                      服务实例
     */
    Object getService(RpcServiceProperties rpcServiceProperties);

    /**
     *
     * @param service  服务实例
     */
    void publishService(Object service);

    /**
     *
     * @param service               服务实例
     * @param rpcServiceProperties  服务实例相关属性
     */
    void publishService(Object service, RpcServiceProperties rpcServiceProperties);
}
