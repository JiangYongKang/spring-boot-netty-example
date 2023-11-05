package com.ykj.server.netty.inbound;

import com.ykj.server.entity.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class InboundMessageDecoder extends LengthFieldBasedFrameDecoder {

    public InboundMessageDecoder(Integer maxFrameLength, Integer lengthFieldOffset, Integer lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext context, ByteBuf byteBuf) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(context, byteBuf);
        if (Objects.isNull(frame)) {
            return null;
        }
        return NettyMessage.buildFormByte(frame);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable throwable) throws Exception {
        context.close();
        super.exceptionCaught(context, throwable);
    }
}
