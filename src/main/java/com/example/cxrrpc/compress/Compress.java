package com.example.cxrrpc.compress;

import com.example.cxrrpc.demo.nettydemo.kryoSerializerDemo.SPI;

/**
 * @Author cxr
 * @Date 2020/12/21 13:45
 */
@SPI
public interface Compress {

    byte[] compress(byte[] bytes);


    byte[] decompress(byte[] bytes);
}
