package com.ykj.server.netty;

import com.ykj.server.entity.NettyMessage;
import com.ykj.server.entity.ProcessorCategory;
import com.ykj.server.netty.inbound.InboundMessageDecoder;
import com.ykj.server.netty.inbound.InboundMessageDecryptor;
import com.ykj.server.netty.inbound.InboundMessageDispatcher;
import com.ykj.server.netty.outbound.OutboundMessageEncoder;
import com.ykj.server.netty.outbound.OutboundMessageEncryptor;
import com.ykj.server.service.InboundMessageService;
import com.ykj.server.service.impl.HeartBeatMessageService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class NettyServerBootstrapRunner {

    @Value("${netty.port}")
    private Integer nettyPort;
    @Value("${netty.secret.key}")
    private String nettySecretKey;

    @Resource
    private HeartBeatMessageService heartBeatMessageService;

    @EventListener(ApplicationStartedEvent.class)
    public void applicationStartedEventListener() {

        log.info("Receive Spring Boot Application Started Event");

        EventLoopGroup bossGroup = new NioEventLoopGroup(2, new DefaultThreadFactory("netty-boss", true));
        EventLoopGroup workGroup = new NioEventLoopGroup(16, new DefaultThreadFactory("netty-work", true));

        try {

            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(this.channelInitializer())
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(nettyPort)).sync();

            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("Netty Server Start On: 127.0.0.1:{}", nettyPort);
                }
            });

            channelFuture.channel().closeFuture().sync();


        } catch (InterruptedException e) {

            log.error("Netty Server Start Fail!", e);

        } finally {

            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();

        }

    }

    private ChannelInitializer<SocketChannel> channelInitializer() {
        return new ChannelInitializer<>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        .addLast(new InboundMessageDecoder(Integer.MAX_VALUE, NettyMessage.LENGTH_FIELD_OFFSET, NettyMessage.LENGTH_FIELD_LENGTH))
                        .addLast(new InboundMessageDecryptor(nettySecretKey))
                        .addLast(dispatchInboundHandler())
                        .addLast(new OutboundMessageEncryptor(nettySecretKey))
                        .addLast(new OutboundMessageEncoder());
            }
        };
    }

    private InboundMessageDispatcher dispatchInboundHandler() {
        Map<Long, InboundMessageService> inboundMessageServiceMap = new ConcurrentHashMap<>();
        inboundMessageServiceMap.put(ProcessorCategory.HEART_BEAT.code(), heartBeatMessageService);
        return new InboundMessageDispatcher(inboundMessageServiceMap);
    }

}
