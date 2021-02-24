package com.example.remoting.transport.netty.client;

import com.example.remoting.entity.ServiceInfo;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author cxr
 * @Date 2020/12/20 17:47
 *
 * 存储和获取 Channel 对象
 */
@Slf4j
public class ChannelProvider {
    private final Map<String, Channel> channelMap;
    private final Map<String, ServiceInfo> monitorMap;

    public ChannelProvider(){
        channelMap = new ConcurrentHashMap<>();
        monitorMap = new ConcurrentHashMap<>();
    }

    public Channel get(InetSocketAddress inetSocketAddress){
        String key = inetSocketAddress.toString();
//        Channel channel = channelMap.get(key);
        // 确认对应地址是否有链接
        if (channelMap.containsKey(key)){
            Channel channel = channelMap.get(key);
            System.out.println(channel);
            // 如果有，确定该连接是否可用，如果可用，则直接获取他
            if (channel != null && channel.isActive()){
                return channel;
            }else {
                channelMap.remove(key);
            }
        }
        // 否则重新连接该 Channel
        NettyRpcClient nettyRpcClient = new NettyRpcClient();
        Channel channel = nettyRpcClient.doConnect(inetSocketAddress);
        channelMap.put(key, channel);
        return channel;
    }

    public void set(InetSocketAddress inetSocketAddress, Channel channel){
        String key = inetSocketAddress.toString();
        channelMap.put(key, channel);
    }

    public void remove(InetSocketAddress inetSocketAddress){
        String key = inetSocketAddress.toString();
        channelMap.remove(key);
        log.info("Channel map 数量：[{}]", channelMap.size());
    }

    // 获取在线的服务
    public Map<String, ServiceInfo> getMonitorMap(){

        return monitorMap;
    }
}
