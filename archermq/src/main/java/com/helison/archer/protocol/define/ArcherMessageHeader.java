package com.helison.archer.protocol.define;
/*
 *@author:wukang
 */

import com.helison.archer.protocol.transport.ChannelId;

import java.util.HashMap;

/*
附加信息将使用字符串进行编码
解码应将其转换为特定的格式
 */
public class ArcherMessageHeader {

    private final Type type;

    private final ChannelId channelId;

    private final int payload;

    private final HashMap<String,Object> extras;


    public ArcherMessageHeader(Type type,ChannelId id,int payload){
        this(type,id,payload,null);
    }
    public ArcherMessageHeader(Type type,ChannelId id,int payload,HashMap<String,Object> extras){
        this.type=type;
        this.channelId=id;
        this.payload=payload;
        this.extras=extras;
    }

    public ChannelId getChannelId() {
        return channelId;
    }

    public int getPayload() {
        return payload;
    }

    public Type getType() {
        return type;
    }

    public HashMap<String,Object> getExtras(){
        return extras;
    }
    public Object getExtraValue(String key){
        if(extras!=null){
            return extras.get(key);
        }
        return null;
    }

    public enum Type{
        /*
        协议头帧
         */
        Protocol(1),
        /*
        方法帧
         */
        Function(2),
        /*
        内容头帧
         */
        Header(3),
        /*
        消息体帧
         */
        Content(4),
        /*
        心跳帧
         */
        HeartBit(5);
        /*
        标识当前帧的类型
         */
        private final int type;

        Type(int type){
            this.type=type;
        }

        public byte getFrameType(){
            return (byte) type;
        }
    }
}
