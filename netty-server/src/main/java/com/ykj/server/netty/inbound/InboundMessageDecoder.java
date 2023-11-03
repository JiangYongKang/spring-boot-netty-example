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
        NettyMessage message = NettyMessage.buildFormByte(frame);
        log.info("Receive Message: {}", message);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
        log.error("Inbound Process Fail", cause);
    }
}
