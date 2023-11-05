package com.ykj.client.controller;

import com.ykj.client.netty.NettyClientBootstrapRunner;
import com.ykj.server.entity.NettyMessage;
import com.ykj.server.entity.NettyServiceEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/message/sender")
public class MessageSenderController {

    @Resource
    private NettyClientBootstrapRunner nettyClientBootstrapRunner;

    @GetMapping
    public ResponseEntity<?> test(@RequestParam("data") String data) {
        NettyMessage message = NettyMessage.buildRequest(NettyServiceEnum.HEART_BEAT.code(), data.getBytes(StandardCharsets.UTF_8));
        nettyClientBootstrapRunner.writeAndFlush(message);
        return ResponseEntity.ok().build();
    }

}
