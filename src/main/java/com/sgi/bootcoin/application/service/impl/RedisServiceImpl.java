package com.sgi.bootcoin.application.service.impl;

import com.sgi.bootcoin.application.service.RedisService;
import com.sgi.bootcoin.domain.model.redis.BootCoinOrder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

    private static final String BOOTCOIN_HASH = "BootCoin";

    private final RedisTemplate<String, Object> redisTemplate;

    private HashOperations<String, String, Object> hashOperations;

    @PostConstruct
    public void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    @CachePut(value = "BootCoin-Order", key = "#operation")
    public Mono<BootCoinOrder> saveBootCoinOrder(String operation, Mono<BootCoinOrder> bootCoinOrderMono) {
        return bootCoinOrderMono.flatMap(this::saveBootCoinToRedis)
                .onErrorResume(error -> {
                    log.error("Error saving bootcoin:", error);
                    return Mono.error(error);
                });
    }

    private Mono<BootCoinOrder> saveBootCoinToRedis(BootCoinOrder bootCoinOrder) {
        return Mono.fromRunnable(() -> hashOperations.put(BOOTCOIN_HASH, bootCoinOrder.getBuyerId(), bootCoinOrder))
                .thenReturn(bootCoinOrder);
    }

    @Override
    @Cacheable(value = "BootCoin-Order", key = "#operation")
    public BootCoinOrder findAllBootCoinOrderByOperation(String operation) {
        return (BootCoinOrder) hashOperations.get(BOOTCOIN_HASH, operation);
    }
}
