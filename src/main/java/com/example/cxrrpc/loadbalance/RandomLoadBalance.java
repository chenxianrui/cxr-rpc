package com.example.cxrrpc.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * @Author cxr
 * @Date 2020/12/27 21:16
 *
 * 随机负载均衡策略的实现
 */
public class RandomLoadBalance extends AbstractLoadBalance{
    @Override
    protected String doSelect(List<String> serviceAddresses) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
