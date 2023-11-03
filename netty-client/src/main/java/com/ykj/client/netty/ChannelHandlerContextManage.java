package com.ykj.client.netty;

import cn.hutool.crypto.SecureUtil;
import com.ykj.server.entity.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Getter
@Component
public class ChannelHandlerContextManage extends ChannelInitializer<SocketChannel> {

    @Value("${netty.aes.key}")
    private String nettyAESKey;

    private ChannelHandlerContext context;

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline()
                .addLast(simpleChannelInboundHandler());
        this.context = channel.pipeline().lastContext();
    }

    private ChannelInboundHandler simpleChannelInboundHandler() {
        return new SimpleChannelInboundHandler<ByteBuf>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                log.info("Receive Message: {}", NettyMessage.buildFormByte(msg));
            }
        };
    }

    public void writeAndFlush(NettyMessage message) {
        if (message.getEncryptedRequest()) {
            byte[] encryptBytes = SecureUtil.aes(nettyAESKey.getBytes(StandardCharsets.UTF_8)).encrypt(message.getData());
            message.setData(encryptBytes);
        }
        log.info("Send Message: {}", message);
        this.context.writeAndFlush(message.toByteBuf());
    }


}
