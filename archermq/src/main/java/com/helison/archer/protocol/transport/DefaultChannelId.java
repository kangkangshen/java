package com.helison.archer.protocol.transport;/*
 *@author:wukang
 */


import io.netty.channel.ChannelId;

/*
使用线程ID作为默认实现
 */
public class DefaultChannelId implements ChannelId {
    private final Thread currentThread;
    private ArcherChannel channel;
    public DefaultChannelId(Thread thread){
        if(thread==null){
            throw new NullPointerException("thread must be not null");
        }
        this.currentThread=thread;
    }
    public DefaultChannelId(Thread thread,ArcherChannel channel){
        this.currentThread=thread;
        this.channel=channel;
    }

    public void setChannel(ArcherChannel channel) {
        this.channel = channel;
    }

    @Override
    public String asShortText() {
        return currentThread.getName();
    }

    @Override
    public String asLongText() {
        return Long.toString(currentThread.getId());
    }

    public Thread getCurrentThread() {
        return currentThread;
    }

    @Override
    public int compareTo(ChannelId o) {
        return (int) (currentThread.getId()-Long.parseLong(o.asLongText()));
    }
}
