package com.ykj.server.netty.inbound;

import com.ykj.server.entity.NettyMessage;
import com.ykj.server.entity.NettyServiceEnum;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@ChannelHandler.Sharable
public class InboundMessageWhitelist extends SimpleChannelInboundHandler<NettyMessage> {

    private final Set<String> whitelist = new HashSet<>();

    public InboundMessageWhitelist(Set<String> whitelist) {
        this.whitelist.addAll(whitelist);
    }


    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        InetSocketAddress socket = (InetSocketAddress) context.channel().remoteAddress();
        String customerHost = socket.getAddress().getHostAddress();
        int customerPort = socket.getPort();
        if (this.whitelist.contains(customerHost)) {
            context.fireChannelActive();
        } else {
            log.warn("UNAUTHORIZED IP ACCESS: {}:{}", customerHost, customerPort);
            NettyMessage message = NettyMessage.buildResponse(NettyServiceEnum.UNAUTHORIZED_IP_ACCESS);
            context.writeAndFlush(message.toByteBuf());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, NettyMessage message) throws Exception {
        context.fireChannelRead(message);
    }

}
