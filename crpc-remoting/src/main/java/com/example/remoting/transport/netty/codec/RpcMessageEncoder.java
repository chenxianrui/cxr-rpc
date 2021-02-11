package com.example.remoting.transport.netty.codec;

import com.example.compress.Compress;
import com.example.enums.CompressTypeEnum;
import com.example.enums.SerializationTypeEnum;
import com.example.extension.ExtensionLoader;
import com.example.remoting.constants.RpcConstants;
import com.example.remoting.dto.RpcMessage;
import com.example.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 自定义协议
 * <p>
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 * @Author cxr
 * @Date 2020/12/20 17:48
 *
 * 自定义协议编码
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage>{
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) throws Exception {
        try {
            out.writeBytes(RpcConstants.MAGIC_NUMBER);      // 写入魔法数
            out.writeByte(RpcConstants.VERSION);           // 写入版本号
            // 留出一个地方写入数据长度
            out.writerIndex(out.writerIndex() + 4);
            byte messageType = rpcMessage.getMessageType();
            out.writeByte(messageType);     // 写入消息类型
            out.writeByte(rpcMessage.getCodec());   // 写入序列化类型
            out.writeByte(CompressTypeEnum.GZIP.getCode()); // 写入压缩类型
            out.writeInt(ATOMIC_INTEGER.getAndIncrement()); // 写入请求id
            // 构造消息长度
            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            // 如果消息类型不是心跳消息，消息长度 =头长+体长
            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE
                    && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE){
                // 序列化对象
                String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                        .getExtension(codecName);
                bodyBytes = serializer.serialize(rpcMessage.getData());
                // 压缩字节
                String compressName = CompressTypeEnum.getName(rpcMessage.getCompress());
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                        .getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }

            if (bodyBytes != null){
                out.writeBytes(bodyBytes);  // 写入消息体
            }
            int writeIndex = out.writerIndex(); // 获取之前记录的索引
            out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
            out.writeInt(fullLength);       // 写入消息长度
            out.writerIndex(writeIndex);
        }catch (Exception e) {
            log.error("编码过程出现错误！", e);
        }
    }
}
