package com.hmd.learnredis.dtos.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Meta {
    private long page;
    private long size;
    private long totalPages;
    private long totalElements;
}
