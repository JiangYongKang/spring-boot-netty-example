package com.ykj.server.netty.outbound;

import cn.hutool.crypto.SecureUtil;
import com.ykj.server.entity.NettyMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@ChannelHandler.Sharable
public class OutboundMessageEncryptor extends MessageToMessageEncoder<NettyMessage> {

    private final String nettySecretKey;

    public OutboundMessageEncryptor(String nettySecretKey) {
        this.nettySecretKey = nettySecretKey;
    }

    @Override
    protected void encode(ChannelHandlerContext context, NettyMessage message, List<Object> out) throws Exception {
        byte[] encryptedBytes = SecureUtil.aes(nettySecretKey.getBytes(StandardCharsets.UTF_8)).encrypt(message.getData());
        message.setData(encryptedBytes);
        out.add(message);
    }
}
