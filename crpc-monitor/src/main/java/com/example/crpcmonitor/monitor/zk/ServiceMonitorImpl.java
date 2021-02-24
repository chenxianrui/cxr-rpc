package com.example.crpcmonitor.monitor.zk;

import com.alibaba.fastjson.JSONObject;
import com.example.crpcmonitor.dto.ServiceInfo;
import com.example.crpcmonitor.monitor.ServiceMonitor;
import com.example.crpcmonitor.rbmq.websocket.WebSocketServer;
import com.example.enums.RpcConfigEnum;
import com.example.factory.SingletonFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.omg.CORBA.ServiceInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @Author cxr
 * @Date 2021/2/18 14:17
 */
@Slf4j
@Service
public class ServiceMonitorImpl implements ServiceMonitor{

    @Autowired
    private WebSocketServer webSocketServer;

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    private static CuratorFramework zkClient;
    private Map<String, ServiceInfo> serviceInfoMap = new ConcurrentHashMap<>();
    private static final String ZK_REGISTER_ROOT_PATH = "/cxr-rpc";  // zookeeper 下的路径
    private static String defaultZookeeperAddress = "47.99.67.211:2181";
    private static int count = 0;

    public ServiceMonitorImpl() throws Exception {
        if (zkClient == null){
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
            zkClient = CuratorFrameworkFactory.builder()
                    .connectString(defaultZookeeperAddress)
                    .retryPolicy(retryPolicy)
                    .build();
            zkClient.start();
            log.info("连接服务：[{}] 失败，启用默认地址。",RpcConfigEnum.ZK_ADDRESS.getPropertyValue());
        }
        updateServiceList();
    }

    @Override
    public void updateServiceList() throws Exception {
        Map<String, ServiceInfo> updateServiceList = new ConcurrentHashMap<>();
        List<ServiceInfo> serviceInfoList = new ArrayList<>();
        List<String> subList = zkClient.getChildren().forPath(ZK_REGISTER_ROOT_PATH);
        List<ServiceInfo> serviceInfos = new ArrayList<>();
        String ip = "";
        for (String subNode:subList){
            ServiceInfo serviceInfo = new ServiceInfo();
            ip = zkClient.getChildren().forPath(ZK_REGISTER_ROOT_PATH+"/"+subNode).get(0);
            serviceInfo.setPath(ZK_REGISTER_ROOT_PATH+"/"+subNode);
            serviceInfo.setIp(ip);
            serviceInfo.setServiceName(subNode);
            // 获取每个子节点下关联的服务器负载信息
            byte[] data = zkClient.getData().forPath(serviceInfo.getPath());
            serviceInfo.setLoadBalance(new String(data, "utf-8"));
            serviceInfoList.add(serviceInfo);
            updateServiceList.put(serviceInfo.getPath(), serviceInfo);
        }
        serviceInfoMap = updateServiceList;
        for (ServiceInfo str : serviceInfoMap.values()) {
            serviceInfos.add(str);
        }
        log.info("更新服务器列表："+serviceInfos);
    }

    @Override
    public void updateServiceLoadBalance(String serverNodePath) throws Exception {
        if (count < 5){
            count++;
            return;
        }
        List<ServiceInfo> serviceInfos = new ArrayList<>();
        ServiceInfo serviceInfo = serviceInfoMap.get(serverNodePath);
        serviceInfo.setGroup(ZK_REGISTER_ROOT_PATH);
        if (serviceInfo != null){
            //获取每个子节点下关联的服务器负载的信息
            byte[] data=zkClient.getData().forPath(serviceInfo.getPath());
            String loadBalance=new String(data,"utf-8");
            serviceInfo.setLoadBalance(loadBalance);
            serviceInfoMap.put(serviceInfo.getPath(), serviceInfo);
            log.info("更新服务器负载信息："+serviceInfo);
            log.info("服务器列表最新信息为："+serviceInfoMap);
        }
        for (ServiceInfo str : serviceInfoMap.values()) {
            serviceInfos.add(str);
        }
        webSocketServer.sendInfo(serviceInfos.toString());
    }

    @Override
    public void handle() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    // 监听事件
    public void watchChild() throws Exception{
        PathChildrenCache childrenCache = new PathChildrenCache(zkClient, ZK_REGISTER_ROOT_PATH, true);
        PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {

            @Override
            public void childEvent(CuratorFramework client,
                                   PathChildrenCacheEvent event) throws Exception {
                ChildData data = event.getData();
                switch (event.getType()) {
                    case CHILD_ADDED:
                        updateServiceLoadBalance(data.getPath());
                        break;
                    case CHILD_REMOVED:
                        updateServiceLoadBalance(data.getPath());
                        break;
                    case CHILD_UPDATED:
                        updateServiceLoadBalance(data.getPath());
                        break;
                    default:
                        break;
                }

            }

        };

        childrenCache.getListenable().addListener(childrenCacheListener);
        log.info("成功注册 watcher!");
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
    }


}
