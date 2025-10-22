package com.hmd.learnredis.controllers;

import com.hmd.learnredis.dtos.InvoiceDTO;
import com.hmd.learnredis.dtos.responses.InvoiceResponse;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @PostMapping("/{secureId}")
    public InvoiceResponse adjust(@Valid @RequestBody InvoiceDTO invoice, @PathVariable String secureId) {
        InvoiceResponse response = new InvoiceResponse();
        response.setSecureId("123456789");
        return response;
    }
}
