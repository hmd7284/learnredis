package com.hmd.learnredis.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseDTO {
    private String message;
    private Object data;
}
