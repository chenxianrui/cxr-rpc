package com.example.hello;

import lombok.*;

import java.io.Serializable;

/**
 * @Author cxr
 * @Date 2021/1/25 14:46
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Hello implements Serializable {
    private String message;
    private String description;
}
