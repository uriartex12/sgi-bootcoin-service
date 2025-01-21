package com.sgi.bootcoin.infrastructure.repository;

import com.sgi.bootcoin.domain.model.BootCoin;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BootcoinRepositoryJpa extends ReactiveMongoRepository<BootCoin, String> {

    Mono<BootCoin> findByPhone(String phone);

    Mono<BootCoin> findByDocumentNumber(String dni);
}
