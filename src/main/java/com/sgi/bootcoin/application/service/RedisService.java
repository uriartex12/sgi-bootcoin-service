package com.sgi.bootcoin.application.service;

import com.sgi.bootcoin.domain.model.redis.BootCoinOrder;
import reactor.core.publisher.Mono;


public interface RedisService {
    Mono<BootCoinOrder> saveBootCoinOrder (String operation, Mono<BootCoinOrder> bootCoinOrderMono);
    BootCoinOrder findAllBootCoinOrderByOperation(String operation);
}
