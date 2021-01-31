package com.example.cxrrpc.demo.nettydemo.encoderso;

import com.example.cxrrpc.demo.nettydemo.kryoSerializerDemo.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * @Author aquarius_cxr
 * @Date 2020/12/17 19:06
 */
@AllArgsConstructor
public class NettyKryoEncoder extends MessageToByteEncoder {
    private final Serializer serializer;
    private final Class<?> genericClass;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (genericClass.isInstance(o)){
            // 1.将对象转换为byte
            byte[] body = serializer.serialize(o);
            // 2.读取消息的长度
            int dataLength = body.length;
            // 3.写入消息对应的字节数组长度，writerIndex 加 4
            byteBuf.writeInt(dataLength);
            // 4.将字节数组写入 ByteBuf 对象中
            byteBuf.writeBytes(body);
        }
    }
}
