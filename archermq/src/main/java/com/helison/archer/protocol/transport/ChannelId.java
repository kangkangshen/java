package com.helison.archer.protocol.transport;

public interface ChannelId extends io.netty.channel.ChannelId {
    int asIntegerID();
}
