package com.hmd.learnredis.services;

import com.hmd.learnredis.dtos.InvoiceDTO;
import com.hmd.learnredis.dtos.InvoiceResult;
import com.hmd.learnredis.dtos.responses.InvoiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalInvoiceService {
    private final WebClient vetcWebClient;

    public Mono<InvoiceResult> adjust(InvoiceDTO invoice) {
        Retry retry = Retry.backoff(3, Duration.ofSeconds(2))
                .filter(this::isRetryable)
                .doBeforeRetry(retrySignal -> log.warn("Retrying call for secureId {}... Attempt #{}",
                        invoice.getSecureId(), retrySignal.totalRetries() + 1));
        log.info("Calling VETC invoice api for secureId {}", invoice.getSecureId());
        return vetcWebClient.post()
                .uri("/{secureId}", invoice.getSecureId())
//                .header(HttpHeaders.AUTHORIZATION, token)
                .body(Mono.just(invoice), InvoiceDTO.class)
                .retrieve()
                .bodyToMono(InvoiceResponse.class)
                .map(response -> {
                            log.info("Successfully adjusted invoice for secureId {}", invoice.getSecureId());
                            return new InvoiceResult(invoice, response, null);
                        }
                )
                .doOnError(error -> log.error("Error adjusting invoice for secureId {}", invoice.getSecureId(), error))
                .retryWhen(retry)
                .onErrorResume(error -> {
                    log.error("All retries failed for secureId {}. Creating error result.", invoice.getSecureId());
                    return Mono.just(new InvoiceResult(invoice, null, error.getMessage()));
                });
    }

    private boolean isRetryable(Throwable throwable) {
        if (throwable instanceof WebClientResponseException) {
            return ((WebClientResponseException) throwable).getStatusCode().is5xxServerError();
        }
        return true;
    }
}
