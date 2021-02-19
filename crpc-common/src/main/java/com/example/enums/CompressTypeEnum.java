package com.example.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author cxr
 * @Date 2020/12/27 21:06
 */
@AllArgsConstructor
@Getter
public enum CompressTypeEnum {

    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

}
