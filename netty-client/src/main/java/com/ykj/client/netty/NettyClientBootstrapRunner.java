package com.ykj.client.netty;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.ykj.server.entity.NettyMessage;
import com.ykj.server.entity.NettyServiceEnum;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class NettyClientBootstrapRunner {

    @Value("${netty.port}")
    private Integer nettyPort;

    @Value("${netty.secret.key}")
    private String nettySecretKey;

    private ChannelHandlerContext context;


    @EventListener(ApplicationStartedEvent.class)
    public void applicationStartedEventListener() {

        log.info("Receive Spring Boot Application Started Event");

        EventLoopGroup workGroup = new NioEventLoopGroup(16, new DefaultThreadFactory("netty-work", true));

        try {

            Bootstrap bootstrap = new Bootstrap()
                    .group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(channelInitializer())
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

    public ChannelInitializer<SocketChannel> channelInitializer() {
        return new ChannelInitializer<>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        .addLast(new LoggingHandler(LogLevel.INFO))
                        .addLast(simpleChannelInboundHandler());
                context = channel.pipeline().firstContext();
            }
        };
    }

    private ChannelInboundHandler simpleChannelInboundHandler() {
        return new SimpleChannelInboundHandler<ByteBuf>() {
            @Override
            public void channelActive(ChannelHandlerContext context) throws Exception {
                NettyMessage message = NettyMessage.buildRequest(
                        NettyServiceEnum.HEART_BEAT.code(),
                        IdUtil.simpleUUID().getBytes(StandardCharsets.UTF_8)
                );
                if (message.getEncryptedRequest()) {
                    byte[] encryptBytes = SecureUtil.aes(nettySecretKey.getBytes(StandardCharsets.UTF_8)).encrypt(message.getData());
                    message.setData(encryptBytes);
                }
                context.writeAndFlush(message.toByteBuf());
                context.fireChannelActive();
            }

            @Override
            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                log.info("Receive Message: {}", NettyMessage.buildFormByte(msg));
            }
        };
    }

    public void writeAndFlush(NettyMessage message) {
        if (message.getEncryptedRequest()) {
            byte[] encryptBytes = SecureUtil.aes(nettySecretKey.getBytes(StandardCharsets.UTF_8)).encrypt(message.getData());
            message.setData(encryptBytes);
        }
        this.context.writeAndFlush(message.toByteBuf());
    }

}
