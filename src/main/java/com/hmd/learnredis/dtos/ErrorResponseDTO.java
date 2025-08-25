package com.hmd.learnredis.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorResponseDTO {
    private String message;
    private Object errorDetails;
}
