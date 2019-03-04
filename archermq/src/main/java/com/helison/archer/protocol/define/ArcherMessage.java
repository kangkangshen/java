package com.helison.archer.protocol.define;/*
 *@author:wukang
 */

/*
定义AMQP消息
 */
public class ArcherMessage {
    //我真是牛逼到爆了 流弊流弊
    public static final short MAGIC_NUM=0x6b6b;
    public static final byte DEFAULT_END_MARK=0X7F;
    public static final int MAX_MSG_LENGTH=Integer.MAX_VALUE;

    private final ArcherMessageHeader header;

    private final Object content;

    public ArcherMessage(ArcherMessageHeader header,Object content){
        this.header=header;
        this.content=content;
    }

    public Object getContent() {
        return content;
    }

    public ArcherMessageHeader getHeader() {
        return header;
    }

}
