package com.hmd.learnredis.services;

import com.hmd.learnredis.dtos.InvoiceDTO;
import com.hmd.learnredis.dtos.InvoiceResult;
import com.hmd.learnredis.dtos.responses.InvoiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalInvoiceService {
    private final RestTemplate restTemplate;
    @Value("${vetc.api.base-url}")
    private String baseUrl;

    public InvoiceResult adjust(InvoiceDTO invoice, String token) {
        try {
            String url = baseUrl + "/{secureId}";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            HttpEntity<InvoiceDTO> request = new HttpEntity<>(invoice, headers);
            log.info("Calling invoice API for invoice {} with secureId {}", invoice.getInvoiceId(), invoice.getSecureId());
            InvoiceResponse response = restTemplate.postForObject(url, request, InvoiceResponse.class, invoice.getSecureId());
            if (response == null) {
                log.warn("Null response for invoice {}", invoice.getInvoiceId());
                return new InvoiceResult(invoice, null, "Null response from API");
            }
            log.info("Successfully adjusted invoice {}: newSecureId={} ", invoice.getInvoiceId(), response.getSecureId());
            return new InvoiceResult(invoice, response, null);
        } catch (RestClientException e) {
            log.error("Error adjusting invoice {}: {}", invoice.getInvoiceId(), e.getMessage());
            return new InvoiceResult(invoice, null, e.getMessage());
        }
    }
}
