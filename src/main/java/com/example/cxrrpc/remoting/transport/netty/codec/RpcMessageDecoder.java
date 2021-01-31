package com.example.cxrrpc.remoting.transport.netty.codec;

import com.example.cxrrpc.compress.Compress;
import com.example.cxrrpc.demo.nettydemo.kryoSerializerDemo.Serializer;
import com.example.cxrrpc.enums.CompressTypeEnum;
import com.example.cxrrpc.enums.SerializationTypeEnum;
import com.example.cxrrpc.extension.ExtensionLoader;
import com.example.cxrrpc.remoting.constants.RpcConstants;
import com.example.cxrrpc.remoting.dto.RpcMessage;
import com.example.cxrrpc.remoting.dto.RpcRequest;
import com.example.cxrrpc.remoting.dto.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

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
 * <p>
 * {@link LengthFieldBasedFrameDecoder} 是一种基于长度的解码器，用于解决TCP解包和粘卡问题。
 * </p>
 * @Author cxr
 * @Date 2020/12/20 17:48
 *
 * 自定义协议编码
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder{

    public RpcMessageDecoder(){
        // lengthFieldOffset: 魔数大小 4 B， 版本信息占 1 B, 所以full length值是 5
        // lengthFieldLength: full length 是4B，所以值是4
        // lengthAdjustment: full length包含所有数据，并且之前读取了9个字节，因此左侧长度为(fullLength-9)。所以值是-9
        // initialBytesToStrip: 我们将手动检查魔数和版本，所以不要剥去任何字节。所以值是0
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    /**
     * @param maxFrameLength      最大帧长度。它决定可以接收的数据的最大长度。
     *                             如果超过，数据将被丢弃。
     * @param lengthFieldOffset   长度字段偏移量。length字段是跳过指定字节长度的字段。
     * @param lengthFieldLength   长度字段中的字节数。
     * @param lengthAdjustment    要添加到length字段值的补偿值
     * @param initialBytesToStrip 跳过的字节数。
     *                             如果需要接收所有header+body数据，则此值为0
     *                             如果您只想接收主体数据，那么您需要跳过头所消耗的字节数。
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in)throws Exception{
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf){
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RpcConstants.TOTAL_LENGTH){
                try {
                    return decodeFrame(frame);
                }catch (Exception e){
                    log.error("解码帧出错！");
                }finally {
                    frame.release();
                }
            }
        }
        return decoded;
    }

    private Object decodeFrame(ByteBuf in){
        // 注: 必须按顺序读取ByteBuf
        // 前 4B 是魔数，读取并比较
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i=0; i<len; i++){
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]){
                throw new IllegalArgumentException("未知的魔数：" + Arrays.toString(tmp));
            }
        }

        // 读取版本并比较
        byte version = in.readByte();
        if (version != RpcConstants.VERSION){
            throw new RuntimeException("不兼容版本" + version);
        }
        int fullLength = in.readInt();
        // 构造 RpcMessage 对象
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        int requesId = in.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .requestId(requesId)
                .messageType(messageType).build();
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE){
            rpcMessage.setData(RpcConstants.PING);
        }else if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE){
            rpcMessage.setData(RpcConstants.PONG);
        }else {
            int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
            if (bodyLength != 0){
                byte[] bs = new byte[bodyLength];
                in.readBytes(bs);
                // 解压缩这些字节
                String compressName = CompressTypeEnum.getName(compressType);
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                        .getExtension(compressName);
                bs = compress.decompress(bs);
                // 反序列化成为对象
                String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                        .getExtension(codecName);
                if (messageType == RpcConstants.REQUEST_TYPE){
                    RpcRequest tmpValue = serializer.deserialize(bs, RpcRequest.class);
                    rpcMessage.setData(tmpValue);
                }else {
                    RpcResponse tmpValue = serializer.deserialize(bs, RpcResponse.class);
                    rpcMessage.setData(tmpValue);
                }
            }
        }
        return rpcMessage;
    }
}
