package com.ykj.server.entity;

import cn.hutool.core.util.RandomUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NettyMessage {

    public static final String NETTY_REQ = "NETTY_REQ";
    public static final String NETTY_RES = "NETTY_RES";

    public static final Integer VERSION_V1 = 1;

    public static final Integer LENGTH_FIELD_OFFSET = 39;
    public static final Integer LENGTH_FIELD_LENGTH = 4;

    private String header;
    private Integer version;
    private String traceId;
    private Long service;
    private Boolean encryptedRequest;
    private Boolean encryptedResponse;
    private Integer contentLength;
    private byte[] data;

    public void setData(byte[] data) {
        this.data = data;
        this.contentLength = data.length;
    }

    public static NettyMessage buildRequest(Long service, byte[] data) {
        return NettyMessage.builder()
                .header(NETTY_REQ)
                .version(VERSION_V1)
                .traceId(RandomUtil.randomString(16))
                .service(service)
                .encryptedRequest(true)
                .encryptedResponse(true)
                .contentLength(data.length)
                .data(data)
                .build();
    }

    public static NettyMessage buildResponse(Long service, byte[] data) {
        return NettyMessage.builder()
                .header(NETTY_RES)
                .version(VERSION_V1)
                .traceId(RandomUtil.randomString(16))
                .service(service)
                .encryptedRequest(true)
                .encryptedResponse(true)
                .contentLength(data.length)
                .data(data)
                .build();
    }

    public static NettyMessage buildResponse(NettyServiceEnum nettyServiceEnum) {
        return NettyMessage.builder()
                .header(NETTY_RES)
                .version(VERSION_V1)
                .traceId(RandomUtil.randomString(16))
                .service(nettyServiceEnum.code())
                .encryptedRequest(true)
                .encryptedResponse(true)
                .contentLength(nettyServiceEnum.symbol().getBytes(StandardCharsets.UTF_8).length)
                .data(nettyServiceEnum.symbol().getBytes(StandardCharsets.UTF_8))
                .build();
    }

    public ByteBuf toByteBuf() {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeCharSequence(this.header, StandardCharsets.UTF_8);
        byteBuf.writeInt(this.version);
        byteBuf.writeCharSequence(this.traceId, StandardCharsets.UTF_8);
        byteBuf.writeLong(this.service);
        byteBuf.writeBoolean(this.encryptedRequest);
        byteBuf.writeBoolean(this.encryptedResponse);
        byteBuf.writeInt(this.data.length);
        byteBuf.writeBytes(this.data);
        return byteBuf;
    }

    public static NettyMessage buildFormByte(ByteBuf byteBuf) {
        return NettyMessage.builder()
                .header(byteBuf.readCharSequence(9, StandardCharsets.UTF_8).toString())
                .version(byteBuf.readInt())
                .traceId(byteBuf.readCharSequence(16, StandardCharsets.UTF_8).toString())
                .service(byteBuf.readLong())
                .encryptedRequest(byteBuf.readBoolean())
                .encryptedResponse(byteBuf.readBoolean())
                .contentLength(byteBuf.readInt())
                .data(ByteBufUtil.getBytes(byteBuf))
                .build();
    }

}
