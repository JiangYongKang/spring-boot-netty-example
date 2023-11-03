package com.ykj.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

@Slf4j
@Component
public class NettyClientBootstrapRunner {

    @Value("${netty.port}")
    private Integer nettyPort;

    @Resource
    private ChannelHandlerContextManage channelHandlerContextManage;

    @EventListener(ApplicationStartedEvent.class)
    public void applicationStartedEventListener() {

        log.info("Receive Spring Boot Application Started Event");

        EventLoopGroup workGroup = new NioEventLoopGroup(16, new DefaultThreadFactory("netty-work", true));

        try {

            Bootstrap bootstrap = new Bootstrap()
                    .group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(channelHandlerContextManage)
                    .option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(nettyPort)).sync();

            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("Netty Client Connect Success");
                }
            });

            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {

            log.error("Netty Client Connect Fail!", e);

        } finally {

            workGroup.shutdownGracefully();

        }

    }

}
