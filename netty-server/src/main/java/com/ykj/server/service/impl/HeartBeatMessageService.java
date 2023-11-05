package com.ykj.server.service.impl;

import cn.hutool.core.util.IdUtil;
import com.ykj.server.entity.NettyMessage;
import com.ykj.server.entity.NettyServiceEnum;
import com.ykj.server.service.InboundMessageService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class HeartBeatMessageService implements InboundMessageService {

    @Override
    public void execute(ChannelHandlerContext context, NettyMessage message) {
        context.executor().scheduleAtFixedRate(() -> {
            NettyMessage response = NettyMessage.buildResponse(NettyServiceEnum.HEART_BEAT.code(), IdUtil.simpleUUID().getBytes(StandardCharsets.UTF_8));
            context.writeAndFlush(response.toByteBuf());
        }, 0, 5000, TimeUnit.MILLISECONDS);
        context.fireChannelRead(message);
    }

}
