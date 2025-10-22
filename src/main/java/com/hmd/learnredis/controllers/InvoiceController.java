package com.hmd.learnredis.controllers;

import com.hmd.learnredis.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/invoice")
public class InvoiceController {
    private final InvoiceService invoiceService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Resource>> readExcel(@RequestPart("file") MultipartFile file) {
        return Mono.fromCallable(file::getInputStream)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(invoiceService::adjustInvoices)
                .map(bytes -> {
                    String fileName = "adjusted_invoice.xlsx";
                    Resource resource = new ByteArrayResource(bytes);
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .body(resource);
                });
    }
}
