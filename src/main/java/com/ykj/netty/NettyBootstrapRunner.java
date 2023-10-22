package com.ykj.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

@Slf4j
@Component
public class NettyBootstrapRunner {

    @Resource
    private ServerChannelInitializer serverChannelInitializer;

    @EventListener(ApplicationStartedEvent.class)
    public void applicationStartedEventListener() {

        log.info("Receive Spring Boot Application Started Event");

        EventLoopGroup bossGroup = new NioEventLoopGroup(2, new DefaultThreadFactory("netty-boss", true));
        EventLoopGroup workGroup = new NioEventLoopGroup(16, new DefaultThreadFactory("netty-work", true));

        try {

            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(this.serverChannelInitializer)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress("127.0.0.1", 8090)).sync();
            log.info("Netty Server Start On: {}:{}", "127.0.0.1", 8090);
            channelFuture.channel().closeFuture().sync();


        } catch (InterruptedException e) {

            log.error("Netty Server Start Fail!", e);

        } finally {

            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();

        }

    }

    @EventListener(ContextClosedEvent.class)
    public void contextClosedEventListener() {

        log.info("Receive Spring Boot Context Closed Event");

    }

}
