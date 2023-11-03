package com.ykj.server.netty.outbound;

import com.ykj.server.entity.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class OutboundMessageEncoder extends MessageToByteEncoder<NettyMessage> {

    @Override
    protected void encode(ChannelHandlerContext context, NettyMessage message, ByteBuf out) throws Exception {
        out.writeBytes(message.toByteBuf());
    }

}
