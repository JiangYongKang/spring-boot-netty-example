package com.ykj.server.netty.inbound;

import com.ykj.server.entity.NettyMessage;
import com.ykj.server.service.InboundMessageService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ChannelHandler.Sharable
public class InboundMessageDispatcher extends SimpleChannelInboundHandler<NettyMessage> {

    private final Map<Long, InboundMessageService> inboundMessageServiceMap = new ConcurrentHashMap<>();

    public InboundMessageDispatcher(Map<Long, InboundMessageService> inboundMessageServiceMap) {
        this.inboundMessageServiceMap.putAll(inboundMessageServiceMap);
    }

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        super.channelActive(context);
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        super.channelInactive(context);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, NettyMessage message) {
        InboundMessageService inboundMessageService = inboundMessageServiceMap.get(message.getService());
        inboundMessageService.execute(context, message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable throwable) throws Exception {
        context.close();
        super.exceptionCaught(context, throwable);
    }
}
