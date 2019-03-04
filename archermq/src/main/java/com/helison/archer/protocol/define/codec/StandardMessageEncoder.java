package com.helison.archer.protocol.define.codec;/*
 *@author:wukang
 */

import com.helison.archer.protocol.define.ArcherMessage;
import com.helison.archer.protocol.define.ArcherMessageHeader;
import io.netty.buffer.*;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.json.JSONObject;

import java.util.HashMap;

/*
默认的编码器编码Archer消息
当对常见的ack ok等进行编码时，直接从缓存池中获取缓存对象
 */

public class StandardMessageEncoder implements MsgEncoder {

    private InternalLogger logger= InternalLoggerFactory.getInstance(StandardMessageEncoder.class);
    private static final ByteBufAllocator DEFAULT_BYTEBUF_ALLOCATOR= new UnpooledByteBufAllocator(true);
    //附加信息段默认使用json编码
    private static final String EXTRAS_ENCODING="json";
    private static final String CONTENT_ENCODING="";

    @Override
    public ByteBuf encode(ArcherMessage msg){
        ArcherMessageHeader header=msg.getHeader();
        int payload=header.getPayload();
        byte frameType=header.getType().getFrameType();
        int channelId=header.getChannelId().asIntegerID();
        if(header.getType().equals(ArcherMessageHeader.Type.Function)){
            short c;
            short f;
        }
        HashMap<String,Object> extras=header.getExtras();
        if(extras!=null&&!extras.isEmpty()){
        }
        return null;
    }

    @Override
    public ByteBuf encode(ArcherMessage msg, boolean preferHeap) {
        return null;
    }

    @Override
    public ByteBuf translate(ArcherMessage msg) {
        return null;
    }


}
