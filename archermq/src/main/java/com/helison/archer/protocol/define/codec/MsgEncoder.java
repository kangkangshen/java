package com.helison.archer.protocol.define.codec;/*
 *@author:wukang
 */

import com.helison.archer.protocol.define.ArcherMessage;
import com.helison.archer.protocol.define.Translater;
import io.netty.buffer.ByteBuf;

/*
将AMQPMsg编译成ByteBuf,默认从堆外内存分配
 */
public interface MsgEncoder extends Translater<ArcherMessage,ByteBuf> {

    ByteBuf encode(ArcherMessage msg);

    ByteBuf encode(ArcherMessage msg,boolean preferHeap);

}
