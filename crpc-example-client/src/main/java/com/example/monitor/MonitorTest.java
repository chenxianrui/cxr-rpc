package com.example.monitor;

import com.example.crpcmonitor.monitor.zk.ServiceMonitorImpl;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;

/**
 * @Author cxr
 * @Date 2021/2/18 15:14
 */
public class MonitorTest {
    public static void main(String[] args) throws Exception {
        ServiceMonitorImpl ac=new ServiceMonitorImpl();
        ac.watchChild();
        ac.handle();
    }
}
