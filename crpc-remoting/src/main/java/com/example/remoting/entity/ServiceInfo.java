package com.example.remoting.entity;

import lombok.*;

/**
 * @Author cxr
 * @Date 2021/2/18 13:35
 *
 * 服务监控
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ServiceInfo {
    // 组
    private String group;
    // 服务节点
    private String path;
    // 服务器名称
    private String serviceName;
    // 服务器负载量
    private String loadBalance;
    // 服务器ip
    private String ip;

    @Override
    public String toString() {
        return  "{"+
                "\"group\":\"" + group + '\"' +
                ", \"path\":\"" + path + '\"' +
                ", \"serviceName\":\"" + serviceName + '\"' +
                ", \"loadBalance\":\"" + loadBalance + '\"' +
                ", \"ip\":\"" + ip + '\"' +
                '}';
    }
}
