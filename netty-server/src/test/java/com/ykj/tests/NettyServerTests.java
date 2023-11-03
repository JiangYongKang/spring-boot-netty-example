package com.ykj.tests;


import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NettyServerTests {

    @Test
    public void inboundTest() {
        String message = "Hello World";
        EmbeddedChannel channel = new EmbeddedChannel(new ServerChannelInboundHandler());
        channel.writeInbound(message);
        Assertions.assertEquals(channel.readOutbound(), message);
        channel.finish();
    }

    @Test
    public void outboundTest() {
        String message = "Hello World";
        EmbeddedChannel channel = new EmbeddedChannel(new ConvertChannelOutboundHandler());
        channel.writeOutbound(message);
        Assertions.assertEquals(channel.readOutbound(), message);
        channel.finish();
    }

}
