package com.example.crpcmonitor.monitor;

import org.apache.zookeeper.KeeperException;

import java.io.UnsupportedEncodingException;

/**
 * @Author cxr
 * @Date 2021/2/18 14:09
 */
public interface ServiceMonitor {

    // 更新服务器列表信息
    public void updateServiceList() throws Exception;

    // 更新服务器节点负载信息
    public void updateServiceLoadBalance(String serverNodePath) throws Exception;

    // client 处理
    public void handle() throws InterruptedException;
}
