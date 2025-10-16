package com.hmd.learnredis.controllers;

import com.hmd.learnredis.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/invoice")
public class InvoiceController {
    private final InvoiceService invoiceService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> readExcel(@RequestPart("file") MultipartFile file) throws Exception {
        invoiceService.readInvoices(file.getInputStream());
        return null;
    }
}
