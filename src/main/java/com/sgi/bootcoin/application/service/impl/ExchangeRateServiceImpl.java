package com.sgi.bootcoin.application.service.impl;

import com.sgi.bootcoin.Infrastructure.mapper.ExchangeRateMapper;
import com.sgi.bootcoin.Infrastructure.repository.ExchangeRateRepositoryJpa;
import com.sgi.bootcoin.domain.port.ExchangeRateService;
import com.sgi.bootcoin.infrastructure.dto.RateRequest;
import com.sgi.bootcoin.infrastructure.dto.RateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepositoryJpa repositoryJpa;

    @Override
    public Mono<RateResponse> createExchangeRate(Mono<RateRequest> rateRequestMono) {
        return rateRequestMono
                .map(ExchangeRateMapper.INSTANCE::map)
                .flatMap(repositoryJpa::save)
                .map(ExchangeRateMapper.INSTANCE::map);
    }

    @Override
    public Flux<RateResponse> getAllExchangeRate() {
        return repositoryJpa.findAll()
                .map(ExchangeRateMapper.INSTANCE::map);
    }

    @Override
    public Mono<RateResponse> getLastExchangeRate() {
        return repositoryJpa.findTopByOrderByCreatedDateDesc()
                .map(ExchangeRateMapper.INSTANCE::map);
    }
}
