package com.example.cxrrpc.enums;

/**
 * @Author cxr
 * @Date 2020/12/27 21:06
 */
public enum RpcConfigEnum {

    RPC_CONFIG_PATH("rpc.properties"),
    ZK_ADDRESS("rpc.zookeeper.address");

    private final String propertyValue;


    RpcConfigEnum(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

}
