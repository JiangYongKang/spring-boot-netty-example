package com.ykj.client.controller;

import com.ykj.client.netty.ChannelHandlerContextManage;
import com.ykj.server.entity.NettyMessage;
import com.ykj.server.entity.ProcessorCategory;
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
    private ChannelHandlerContextManage channelHandlerContextManage;

    @GetMapping
    public ResponseEntity<?> test(@RequestParam("data") String data) {
        NettyMessage message = NettyMessage.buildRequest(ProcessorCategory.HEART_BEAT.code(), data.getBytes(StandardCharsets.UTF_8));
        channelHandlerContextManage.writeAndFlush(message);
        return ResponseEntity.ok().build();
    }

}
