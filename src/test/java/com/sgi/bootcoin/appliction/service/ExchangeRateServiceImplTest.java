package com.sgi.bootcoin.appliction.service;

import com.sgi.bootcoin.infrastructure.mapper.ExchangeRateMapper;
import com.sgi.bootcoin.infrastructure.repository.ExchangeRateRepositoryJpa;
import com.sgi.bootcoin.application.service.impl.ExchangeRateServiceImpl;
import com.sgi.bootcoin.domain.model.ExchangeRate;
import com.sgi.bootcoin.helper.FactoryTest;
import com.sgi.bootcoin.infrastructure.dto.RateRequest;
import com.sgi.bootcoin.infrastructure.dto.RateResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceImplTest {

    @Mock
    private ExchangeRateRepositoryJpa repositoryJpa;

    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateService;


    @Test
    void createExchangeRate_shouldReturnRateResponse() {
        RateRequest rateRequestMono = FactoryTest.toFactoryExchangeRate(RateRequest.class);
        ExchangeRate exchangeRate = ExchangeRateMapper.INSTANCE.map(rateRequestMono);
        RateResponse rateResponse = ExchangeRateMapper.INSTANCE.map(exchangeRate);

        when(repositoryJpa.save(any(ExchangeRate.class))).thenReturn(Mono.just(exchangeRate));

        Mono<RateResponse> result = exchangeRateService.createExchangeRate(Mono.just(rateRequestMono));

        StepVerifier.create(result)
                .expectNext(rateResponse)
                .verifyComplete();

        verify(repositoryJpa, times(1)).save(any(ExchangeRate.class));
    }

    @Test
    void getAllExchangeRate_shouldReturnRateResponse(){
        RateRequest rateRequestMono = FactoryTest.toFactoryExchangeRate(RateRequest.class);
        ExchangeRate exchangeRate = ExchangeRateMapper.INSTANCE.map(rateRequestMono);

        when(repositoryJpa.findAll()).thenReturn(Flux.just(exchangeRate));

        Flux<RateResponse> result = exchangeRateService.getAllExchangeRate();

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(repositoryJpa, times(1)).findAll();
    }

    @Test
    void getLastExchangeRate_shouldReturnRateResponse() {
        RateRequest rateRequest = FactoryTest.toFactoryExchangeRate(RateRequest.class);
        ExchangeRate exchangeRate = ExchangeRateMapper.INSTANCE.map(rateRequest);
        RateResponse expectedResponse = ExchangeRateMapper.INSTANCE.map(exchangeRate);

        when(repositoryJpa.findTopByOrderByCreatedDateDesc()).thenReturn(Mono.just(exchangeRate));

        Mono<RateResponse> result = exchangeRateService.getLastExchangeRate();

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(repositoryJpa, times(1)).findTopByOrderByCreatedDateDesc();
    }

}
