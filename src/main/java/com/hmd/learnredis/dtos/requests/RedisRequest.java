package com.hmd.learnredis.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RedisRequest {
    @NotBlank(message = "Key is required")
    private String key;

    @NotBlank(message = "Value is required")
    private Object value;

    private Long ttl;
}
