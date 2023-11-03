package com.ykj.server.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public enum ProcessorCategory {

    HEART_BEAT(1_000L, "HEART_BEAT", "用于发送和接收心跳");

    private final Long code;
    private final String symbol;
    private final String description;

}
