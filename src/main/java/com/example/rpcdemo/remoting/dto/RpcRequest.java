package com.example.rpcdemo.remoting.dto;

import com.example.rpcdemo.remoting.entity.RpcServiceProperties;
import lombok.*;

import java.io.Serializable;

/**
 * @Author cxr
 * @Date 2020/12/18 18:38
 *
 * 网络传输对象
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Builder
public class RpcRequest implements Serializable{
    private static final long serialVersionUID = 1905122041950251207L;
    private String requestId;       // 请求id
    private String interfaceName;   // 目标接口
    private String methodName;      // 目标方法
    private Object[] parameters;     // 参数
    private Class<?> paramTypes;    // 参数类型
    private String version;         // 服务版本（主要是为后续不兼容升级提供可能）
    private String group;           // 用于处理一个接口有多个类实现的情况

    public RpcServiceProperties toRpcProperties(){
        return RpcServiceProperties.builder().serviceName(this.getInterfaceName())
                .version(this.getVersion())
                .group(this.getGroup()).build();
    }
}
