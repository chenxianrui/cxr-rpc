package com.example.crpcmonitor.dto;

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
    // 服务节点
    private String path;
    // 服务器名称
    private String serviceName;
    // 服务器负载量
    private String loadBalance;

    @Override
    public String toString() {
        return " [服务器节点路径=" + path + ", 服务器名称=" + serviceName + ", 服务器负载=" + loadBalance + "]";
    }

}
