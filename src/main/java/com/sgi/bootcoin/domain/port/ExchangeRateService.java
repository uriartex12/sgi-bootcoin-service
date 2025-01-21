package com.sgi.bootcoin.domain.port;

import com.sgi.bootcoin.infrastructure.dto.RateRequest;
import com.sgi.bootcoin.infrastructure.dto.RateResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ExchangeRateService {
    Mono<RateResponse>  createExchangeRate(Mono<RateRequest> rateRequestMono);
    Flux<RateResponse> getAllExchangeRate();
    Mono<RateResponse> getLastExchangeRate();
}
