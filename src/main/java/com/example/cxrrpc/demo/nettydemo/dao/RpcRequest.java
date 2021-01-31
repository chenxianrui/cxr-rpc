package com.example.cxrrpc.demo.nettydemo.dao;

import lombok.*;

/**
 * @Author aquarius_cxr
 * @Date 2020/12/17 17:52
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@ToString
public class RpcRequest {
    private String interfaceName;
    private String methodName;
}
