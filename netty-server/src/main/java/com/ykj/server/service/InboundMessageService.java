package com.ykj.server.service;

import com.ykj.server.entity.NettyMessage;
import io.netty.channel.ChannelHandlerContext;

public interface InboundMessageService {

    void execute(ChannelHandlerContext context, NettyMessage message);

}
