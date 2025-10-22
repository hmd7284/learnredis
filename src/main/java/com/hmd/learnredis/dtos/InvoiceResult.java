package com.hmd.learnredis.dtos;

import com.hmd.learnredis.dtos.responses.InvoiceResponse;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResult {
    private InvoiceDTO invoice;
    private InvoiceResponse response;
    private String error;
}
