package com.example.registry.zk;

import com.example.enums.RpcErrorMessageEnum;
import com.example.exception.RpcException;
import com.example.loadbalance.LoadBalance;
import com.example.loadbalance.RandomLoadBalance;
import com.example.registry.ServiceDiscovery;
import com.example.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author cxr
 * @Date 2020/12/27 21:02
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {
    private final static String ZK_DEFAULT_PATH = "/cxr-rpc/";
    private final LoadBalance loadBalance;

    public ZkServiceDiscovery(){
        this.loadBalance = new RandomLoadBalance();
    }

    @Override
    public InetSocketAddress lookupService(String rpcServiceName) {
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (serviceUrlList.size() == 0) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        // 负载均衡
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList);
        log.info("成功发现服务地址:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        // 使用节点增加一次
        try {
            byte[] oldLoadData = zkClient.getData().forPath(ZK_DEFAULT_PATH+rpcServiceName);
            if (oldLoadData == null){
                zkClient.setData().forPath(ZK_DEFAULT_PATH+rpcServiceName, String.valueOf(1).getBytes());
            }else {
                Integer loadData = Integer.valueOf(new String(oldLoadData, "utf-8"))+1;
                zkClient.setData().forPath(ZK_DEFAULT_PATH+rpcServiceName, String.valueOf(loadData).getBytes());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
