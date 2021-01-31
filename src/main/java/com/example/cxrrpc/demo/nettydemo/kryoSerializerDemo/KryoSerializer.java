package com.example.cxrrpc.demo.nettydemo.kryoSerializerDemo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.example.cxrrpc.demo.nettydemo.dao.RpcRequest;
import com.example.cxrrpc.demo.nettydemo.dao.RpcResponse;
import com.example.cxrrpc.demo.nettydemo.exception.SerializeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @Author aquarius_cxr
 * @Date 2020/12/17 18:07
 */
public class KryoSerializer implements Serializer{

    /**
     * 由于 Kryo 不是线程安全的。每个线程都应该有自己的 Kryo， Input 和 output 实例。
     * 所以，使用 ThreadLocal 存放 Kryo 对象
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() ->{
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.setReferences(true); // 默认值为true，是否关闭注册行为，关闭之后可能存在序列化问题，一般推荐设置为true
        kryo.setRegistrationRequired(false);// 默认值为false，是否关闭循环引用，可以提高性能，但是一般推荐设置为true
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream)){
            // 获取kryo
            Kryo kryo = kryoThreadLocal.get();
            // Object->byte:将对象序列化为byte数组
            kryo.writeObject(output, obj);
            // 移除kryo
            kryoThreadLocal.remove();
            return output.toBytes();
        }catch (Exception e){
            throw new SerializeException("序列化失败");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream)){
            Kryo kryo = kryoThreadLocal.get();
            // byte->Objetc:从byte数组中反序列化出对象
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return clazz.cast(o);
        }catch (Exception e){
            throw new SerializeException("反序列化失败");
        }
    }
}
