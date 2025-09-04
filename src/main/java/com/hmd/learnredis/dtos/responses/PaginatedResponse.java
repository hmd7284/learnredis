package com.hmd.learnredis.dtos.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaginatedResponse {
    private Meta meta;
    private Object data;
}
