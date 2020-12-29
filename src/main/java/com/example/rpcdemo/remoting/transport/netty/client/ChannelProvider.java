package com.example.rpcdemo.remoting.transport.netty.client;

import com.example.rpcdemo.demo.nettydemo.client.NettyClient;
import com.example.rpcdemo.factory.SingletonFactory;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author cxr
 * @Date 2020/12/20 17:47
 *
 * 存取和获取 Channel 对象
 */
@Slf4j
public class ChannelProvider {
    private final Map<String, Channel> channelMap;

    public ChannelProvider(){
        channelMap = new ConcurrentHashMap<>();
    }

    public Channel get(InetSocketAddress inetSocketAddress){
        String key = inetSocketAddress.toString();
        // 确认对应地址是否有链接
        if (channelMap.containsKey(key)){
            Channel channel = channelMap.get(key);
            // 如果有，确定该连接是否可用，如果可用，则直接获取他
            if (channel != null && channel.isActive()){
                return channel;
            }else {
                channelMap.remove(key);
            }
        }
        // 否则重新连接该 Channel
//        Channel channel = NettyRpcClient.doConnect(inetSocketAddress);
//        channelMap.put(key, channel);
        return null;
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
}
