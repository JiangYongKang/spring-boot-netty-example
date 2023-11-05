package com.ykj.server.netty.inbound;

import cn.hutool.crypto.SecureUtil;
import com.ykj.server.entity.NettyMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
@ChannelHandler.Sharable
public class InboundMessageDecryptor extends SimpleChannelInboundHandler<NettyMessage> {

    private final String nettySecretKey;

    public InboundMessageDecryptor(String nettySecretKey) {
        this.nettySecretKey = nettySecretKey;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, NettyMessage message) {
        if (message.getEncryptedRequest()) {
            byte[] decryptBytes = SecureUtil.aes(nettySecretKey.getBytes(StandardCharsets.UTF_8)).decrypt(message.getData());
            message.setData(decryptBytes);
        }
        context.fireChannelRead(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable throwable) throws Exception {
        context.close();
        super.exceptionCaught(context, throwable);
    }
}
