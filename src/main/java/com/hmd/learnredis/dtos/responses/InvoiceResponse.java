package com.hmd.learnredis.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceResponse {
    @JsonProperty(value = "secure_id")
    private String secureId;
}

