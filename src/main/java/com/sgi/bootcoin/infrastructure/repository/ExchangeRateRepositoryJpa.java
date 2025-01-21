package com.sgi.bootcoin.infrastructure.repository;

import com.sgi.bootcoin.domain.model.ExchangeRate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ExchangeRateRepositoryJpa extends ReactiveMongoRepository<ExchangeRate, String> {
    Mono<ExchangeRate> findTopByOrderByCreatedDateDesc();
}
