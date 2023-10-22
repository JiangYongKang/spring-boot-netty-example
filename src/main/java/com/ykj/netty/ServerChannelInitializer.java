package com.ykj.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Resource
    private ServerChannelInboundHandler serverChannelInboundHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast("DECODE", new StringDecoder(CharsetUtil.UTF_8))
                .addLast("ENCODE", new StringEncoder(CharsetUtil.UTF_8))
                .addLast("DEFAULT", this.serverChannelInboundHandler);
    }

}
