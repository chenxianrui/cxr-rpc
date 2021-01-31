package com.example.cxrrpc.registry.zk;

import com.example.cxrrpc.registry.ServiceRegistry;
import com.example.cxrrpc.registry.zk.util.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * @Author cxr
 * @Date 2020/12/27 21:03
 */
public class ZkServiceRegistry implements ServiceRegistry{
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" +rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }
}
