package com.example.crpcmonitor.demo;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
/**
 * @Author cxr
 * @Date 2021/2/17 22:07
 */
public class AppServer {
    /**
     * zookeeper中集群服务器的总节点
     */
    private String groupNode = "sgroup";
    private ZooKeeper zooKeeper;
    /**
     * 服务器创建的节点的路径
     */
    private String serverNodePath="";
    /**
     * 当前服务器的负载
     */
    private int loadBalance=0;
    /**
     * 连接zookeeper服务器，并在集群总结点下创建EPHEMERAL类型的子节点，把服务器名称存入子节点的数据
     * @param zookeeperServerHost
     * @param serverName
     * @throws IOException
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void connectZookeeper(String zookeeperServerHost, String serverName)
            throws IOException, KeeperException, InterruptedException {
        zooKeeper = new ZooKeeper(zookeeperServerHost, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                // 啥都不做

            }
        });
        // 先判断sgroup节点是否存在
        String groupNodePath = "/" + groupNode;
        Stat stat = zooKeeper.exists(groupNodePath, false);
        if (null == stat) {
            zooKeeper.create(groupNodePath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        // 将server的地址数据关联到新创建的子节点上
        serverNodePath=zooKeeper.create(groupNodePath+"/"+serverName,
                String.valueOf(loadBalance).getBytes("utf-8"), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("创建了server节点："+serverNodePath);
        //定时上传服务器的负载
        uploadBarance();
    }
    /**
     * 关闭于zookeeper服务器的连接
     * @throws InterruptedException
     */
    public void closeZookeeper() throws InterruptedException{
        if(null!=zooKeeper){
            zooKeeper.close();
        }
    }
    /**
     * 每隔10秒上传一次负载
     *
     */
    private void uploadBarance(){
        new Thread(new  Runnable() {
            public void run() {
                while(true){
                    try {
                        Thread.sleep(10000);
                        loadBalance=new Random().nextInt(100000);
                        String l=String.valueOf(loadBalance);
                        System.out.println("服务器上传负载："+loadBalance);
                        zooKeeper.setData(serverNodePath, l.getBytes("utf-8"), -1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();
    }

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        System.out.print("请输入服务器名称（如server001）:");
        Scanner scan = new Scanner(System.in);
        String serverName = scan.nextLine();
        AppServer appServer=new AppServer();
        appServer.connectZookeeper("47.99.67.211:2181", serverName);
        while(true){
            System.out.println("请输入您的操作指令(exit 退出系统)：");
            String command = scan.nextLine();
            if("exit".equals(command)){
                System.out.println("服务器关闭中....");
                appServer.zooKeeper.close();
                System.exit(0);
                break;
            }else{
                continue;
            }
        }
    }
}
