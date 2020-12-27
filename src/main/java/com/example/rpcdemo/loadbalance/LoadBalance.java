package com.example.rpcdemo.loadbalance;

import java.util.List;

/**
 * @Author cxr
 * @Date 2020/12/27 21:12
 *
 * 接口配置负载均衡策略
 */
public interface LoadBalance {
    /**
     * 从现有服务地址列表中选择一个
     *
     * @param serviceAddress 服务地址列表
     * @return 标记服务地址
     */
    String selectServiceAddress(List<String> serviceAddress);
}
