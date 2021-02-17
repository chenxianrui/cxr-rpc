package com.example.crpcmonitor.demo;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
/**
 * @Author cxr
 * @Date 2021/2/17 22:08
 */
public class AppServerMonitor implements Watcher{
    private String groupNode = "sgroup";
    private ZooKeeper zk;
    private Stat stat = new Stat();
    //服务器信息，包含了服务器名称、负载两个信息,使用map存储，key是服务器节点path,value是服务器信息对象
    private volatile Map<String,ServerInfo> serverList=new TreeMap<String, ServerInfo>();

    /**
     * 连接zookeeper服务器
     *
     * @throws IOException
     * @throws InterruptedException
     * @throws KeeperException
     */
    public void connectZookeeper() throws IOException, KeeperException, InterruptedException {
        zk = new ZooKeeper("47.99.67.211:2181", 5000, this);
        //查看要检测的服务器集群的根节点是否存在，如果不存在，则创建
        if(null==zk.exists("/"+groupNode, false)){
            zk.create("/"+groupNode, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        updateServerList();
    }
    /**
     * 更新服务器列表信息
     * @throws KeeperException
     * @throws InterruptedException
     * @throws UnsupportedEncodingException
     */
    private void updateServerList() throws KeeperException, InterruptedException, UnsupportedEncodingException {
        Map<String,ServerInfo> newServerList=new TreeMap<String,ServerInfo>();
        // 获取并监听groupNode的子节点变化
        // watch参数为true, 表示监听子节点变化事件.
        // 每次都需要重新注册监听, 因为一次注册, 只能监听一次事件, 如果还想继续保持监听, 必须重新注册
        List<String> subList=zk.getChildren("/"+groupNode,true);
        for(String subNode:subList){
            ServerInfo serverInfo=new ServerInfo();
            serverInfo.setPath("/"+groupNode+"/"+subNode);
            serverInfo.setName(subNode);
            //获取每个子节点下关联的服务器负载的信息
            byte[] data=zk.getData(serverInfo.getPath(), true, stat);
            String loadBalance=new String(data,"utf-8");
            serverInfo.setLoadBalance(loadBalance);
            newServerList.put(serverInfo.getPath(), serverInfo);

        }
        // 替换server列表
        serverList=newServerList;
        System.out.println("$$$更新了服务器列表:"+serverList);
    }
    /**
     * 更新服务器节点的负载数据
     * @param serverNodePath
     * @throws InterruptedException
     * @throws KeeperException
     * @throws UnsupportedEncodingException
     */
    private void updateServerLoadBalance(String serverNodePath) throws KeeperException, InterruptedException, UnsupportedEncodingException{
        ServerInfo serverInfo=serverList.get(serverNodePath);
        if(null!=serverInfo){
            //获取每个子节点下关联的服务器负载的信息
            byte[] data=zk.getData(serverInfo.getPath(), true, stat);
            String loadBalance=new String(data,"utf-8");
            serverInfo.setLoadBalance(loadBalance);
            serverList.put(serverInfo.getPath(), serverInfo);
            System.out.println("@@@更新了服务器的负载："+serverInfo);
            System.out.println("------");
            System.out.println("###更新服务器负载后，服务器列表信息："+serverList);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("监听到zookeeper事件-----eventType:"+event.getType()+",path:"+event.getPath());
        //集群总节点的子节点变化触发的事件
        if (event.getType() == EventType.NodeChildrenChanged &&
                event.getPath().equals("/" + groupNode)) {
            //如果发生了"/sgroup"节点下的子节点变化事件, 更新server列表, 并重新注册监听
            try {
                updateServerList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (event.getType() == EventType.NodeDataChanged &&
                event.getPath().startsWith("/" + groupNode)) {
            //如果发生了服务器节点数据变化事件, 更新server列表, 并重新注册监听
            try {
                updateServerLoadBalance(event.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * client的工作逻辑写在这个方法中
     * 此处不做任何处理, 只让client sleep
     * @throws InterruptedException
     */
    public void handle() throws InterruptedException{
        Thread.sleep(Long.MAX_VALUE);
    }
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        AppServerMonitor ac=new AppServerMonitor();
        ac.connectZookeeper();
        ac.handle();
    }
    /**
     * 内部类，服务器信息
     * @author Administrator
     *
     */
    class ServerInfo{
        //服务节点在zookeeper上的路径
        private String path;
        //服务器名称
        private String name;
        //服务器负载量
        private String loadBalance;

        public String getPath() {
            return path;
        }
        public void setPath(String path) {
            this.path = path;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getLoadBalance() {
            return loadBalance;
        }
        public void setLoadBalance(String loadBalance) {
            this.loadBalance = loadBalance;
        }
        @Override
        public String toString() {
            return " [服务器节点路径=" + path + ", 服务器名称=" + name + ", 服务器负载=" + loadBalance + "]";
        }




    }

}
