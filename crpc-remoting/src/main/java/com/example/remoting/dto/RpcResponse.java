package com.example.remoting.dto;

import com.example.enums.RpcResponseCodeEnum;
import lombok.*;

import java.io.Serializable;

/**
 * @Author cxr
 * @Date 2020/12/18 18:39
 *
 * 网络传输对象
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = 715745410605631233L;
    private String requestId;

    /**
     * 响应代码
     */
    private Integer code;

    /**
     * 响应内容
     */
    private String message;

    /**
     * 响应体
     */
    private T data;

    public static <T> RpcResponse<T> success(T data, String requestId){
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResponseCodeEnum.SUCCESS.getCode());
        response.setMessage(RpcResponseCodeEnum.SUCCESS.getMessage());
        response.setRequestId(requestId);
        if (data != null){
            response.setData(data);
        }
        return response;
    }

    public static <T> RpcResponse<T> fail(RpcResponseCodeEnum rpcResponseCodeEnum){
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(rpcResponseCodeEnum.getCode());
        response.setMessage(rpcResponseCodeEnum.getMessage());
        return response;
    }

}
