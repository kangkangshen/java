package com.helison.archer.protocol;/*
 *@author:wukang
 */

import com.helison.archer.protocol.define.ArcherMessage;
import com.helison.archer.utils.Assert;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.HashMap;
import java.util.Set;

/*
常用的archer消息缓存池，用于产生像某些成功/失败ack,心跳包等模板包，并预置字节缓冲并提供接口修改后添加的缓冲
 */
public final class ArcherProtocolInstancePool {
    private static final HashMap<String, ByteBuf> instancePool=new HashMap<>(16);
    private static final int BUFFER_CEILING=1<<8;
    /*
    参数分别代表连接建立成功，交换器创建成功，队列声明成功，队列与交换器绑定成功，消息消费成功，消息取消成功，消息投递成功
    因为往往是成功的响应和心跳帧占绝大多数，因此只缓存成功的实例
     */
    private static final String[] FUNDAMENTAL_TEMPLATE_NAMES={"startOk","createOk","declareOk","bindOk","deliverOk","consumeOk","cancelOk","ping","pong"};

    public void addMsgBuffer(int type, ByteBuf buffer){
        Assert.notNull(buffer);
        if(!isFundamentalTemplate(Integer.toString(type))){
            instancePool.put(Integer.toString(type),buffer);
        }
    }

    public void removeMsgBuffer(int type,ByteBuf buffer){
        if(!isFundamentalTemplate(Integer.toString(type))){
            instancePool.remove(Integer.toString(type),buffer);
        }
    }

    private boolean isFundamentalTemplate(String type){
        for(String templateName:FUNDAMENTAL_TEMPLATE_NAMES){
            if(templateName.equals(type)){
                return true;
            }
        }
        return false;
    }

    public Set<String> getInstanceNames(){
        return instancePool.keySet();
    }

    static {
        //init instance pool,default alloc direct buffer
        ByteBuf[] bufs=new ByteBuf[FUNDAMENTAL_TEMPLATE_NAMES.length];
        for(int i=0;i<FUNDAMENTAL_TEMPLATE_NAMES.length;i++){
            bufs[i]= Unpooled.directBuffer(16);
            bufs[i].writeShort(ArcherMessage.MAGIC_NUM).writeByte(1).skipBytes(4).writeInt(16).skipBytes(4).writeByte(ArcherMessage.DEFAULT_END_MARK);
            instancePool.put(FUNDAMENTAL_TEMPLATE_NAMES[i],bufs[i]);
        }
        createStartOk(bufs[0]);
        createCreateOk(bufs[1]);
        createDeclareOk(bufs[2]);
        createBindOk(bufs[3]);
        createDeliverOk(bufs[4]);
        createConsumeOk(bufs[5]);
        createCanceleOk(bufs[6]);
        createPing(bufs[7]);
        createPong(bufs[8]);
    }

    static ByteBuf createStartOk(ByteBuf buffer){
        buffer.setShort(11,0);
        buffer.setShort(13,1);
        return buffer;
    }
    static ByteBuf createCreateOk(ByteBuf buffer){
        buffer.setShort(11,1);
        buffer.setShort(13,1);
        return buffer;
    }
    static ByteBuf createDeclareOk(ByteBuf buffer){
        buffer.setShort(11,2);
        buffer.setShort(13,1);
        return buffer;
    }
    static ByteBuf createBindOk(ByteBuf buffer){
        buffer.setShort(11,3);
        buffer.setShort(13,1);
        return buffer;
    }
    static ByteBuf createDeliverOk(ByteBuf buffer){
        buffer.setShort(11,4);
        buffer.setShort(13,1);
        return buffer;
    }
    static ByteBuf createConsumeOk(ByteBuf buffer){
        buffer.setShort(11,5);
        buffer.setShort(13,1);
        return buffer;
    }
    static ByteBuf createCanceleOk(ByteBuf buffer){
        buffer.setShort(11,6);
        buffer.setShort(13,1);
        return buffer;
    }
    static ByteBuf createPing(ByteBuf buffer){
        buffer.setShort(11,7);
        buffer.setShort(13,0);
        return buffer;
    }
    static ByteBuf createPong(ByteBuf buffer){
        buffer.setShort(11,7);
        buffer.setShort(13,1);
        return buffer;

    }
}
