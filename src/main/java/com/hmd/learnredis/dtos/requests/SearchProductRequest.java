package com.hmd.learnredis.dtos.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchProductRequest extends SearchRequest {
    private String name;
    private Double priceFrom;
    private Double priceTo;
    private Boolean sortByPrice = false;
    private Boolean priceDesc = true;
}
