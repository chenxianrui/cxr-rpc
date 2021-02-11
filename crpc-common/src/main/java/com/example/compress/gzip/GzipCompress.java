package com.example.compress.gzip;


import com.example.compress.Compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Author cxr
 * @Date 2020/12/25 20:35
 */
public class GzipCompress implements Compress {


    private static final int BUFFER_SIZE = 1024 * 4;

    @Override
    public byte[] compress(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("字节为空");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(bytes);
            gzip.flush();
            gzip.finish();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("gzip compress 出现错误", e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("字节为空");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPInputStream gunzip = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while ((n = gunzip.read(buffer)) > -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("gzip decompress 出现错误", e);
        }
    }
}
