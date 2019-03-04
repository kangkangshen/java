package com.helison.archer.protocol.transport;/*
 *@author:wukang
 */


import com.helison.archer.protocol.define.ArcherMessage;
import io.netty.channel.ChannelFuture;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;

/*
定义AMQP channel概念，依赖池化，返回当前调用线程标识的的虚拟信道
底层保持一个tcp连接，该接口提供应用层的抽象，是线程级别的，轻量级的连接
 */
public interface ArcherChannel extends Channel {

    ChannelId getId();

    SocketChannel getTcpConnection();

    boolean isValid();

    boolean isClosed();

    ChannelFuture writeMsg(ArcherMessage archerMessage);









}
