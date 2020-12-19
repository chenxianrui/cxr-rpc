package com.example.rpcdemo.demo.zookeapdemo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @Author cxr
 * @Date 2020/12/18 17:46
 */
public class ZookeapUse {

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;

    public static void main(String[] args) throws Exception {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                // 连接至服务端
                .connectString("10.10.44.193:2181")
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/node1/00005","java".getBytes());
        zkClient.getData().forPath("/node1/00005");//获取节点的数据内容
        zkClient.setData().forPath("/node1/00005","c++".getBytes());//更新节点数据内容
    }
}
