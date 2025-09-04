package com.hmd.learnredis.dtos.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequest {
    private int page = 1;
    private int pageSize = 20;
}
