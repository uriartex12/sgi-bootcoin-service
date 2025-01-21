package com.sgi.bootcoin.appliction.service;

import com.sgi.bootcoin.application.service.impl.RedisServiceImpl;
import com.sgi.bootcoin.domain.model.redis.BootCoinOrder;
import com.sgi.bootcoin.helper.FactoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static com.mongodb.assertions.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisServiceImplTest {


    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, String, Object> hashOperations;

    @InjectMocks
    private RedisServiceImpl redisService;

    private static final String REDIS_HASH = "BootCoin";

    @BeforeEach
    void setUp() {
        doReturn(hashOperations).when(redisTemplate).opsForHash();
        redisService.init();
    }

    @Test
    void saveBootCoinOrder_shouldSaveBootCoinOrder() {
        String operation = UUID.randomUUID().toString();
        BootCoinOrder bootCoinOrder = FactoryTest.toFactoryBootcoinOrder();
        bootCoinOrder.setId(operation);

        Mono<BootCoinOrder> bootCoinOrderMono = Mono.just(bootCoinOrder);
        doNothing().when(hashOperations).put(REDIS_HASH, operation, bootCoinOrder);
        Mono<BootCoinOrder> result = redisService.saveBootCoinOrder(operation, bootCoinOrderMono);

        StepVerifier.create(result)
                .expectNext(bootCoinOrder)
                .verifyComplete();

        verify(hashOperations, times(1)).put(REDIS_HASH, operation, bootCoinOrder);
    }

    @Test
    void saveBootCoinOrder_shouldHandleErrorGracefully() {
        String operation = UUID.randomUUID().toString();
        BootCoinOrder bootCoinOrder = FactoryTest.toFactoryBootcoinOrder();
        bootCoinOrder.setId(operation);

        Mono<BootCoinOrder> bootCoinOrderMono = Mono.just(bootCoinOrder);

        doThrow(new RuntimeException("Redis error")).when(hashOperations).put(REDIS_HASH, operation, bootCoinOrder);

        Mono<BootCoinOrder> result = redisService.saveBootCoinOrder(operation, bootCoinOrderMono);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(hashOperations, times(1)).put(REDIS_HASH, operation, bootCoinOrder);
    }

    @Test
    void findAllBootCoinOrderByOperation_shouldReturnBootCoinOrder() {
        String operation = UUID.randomUUID().toString();
        BootCoinOrder bootCoinOrder = FactoryTest.toFactoryBootcoinOrder();
        bootCoinOrder.setId(operation);

        when(hashOperations.get(REDIS_HASH, operation)).thenReturn(bootCoinOrder);

        BootCoinOrder result = redisService.findAllBootCoinOrderByOperation(operation);

        assertNotNull(result);
        assertEquals(operation, result.getId());

        verify(hashOperations, times(1)).get(REDIS_HASH, operation);
    }

    @Test
    void findAllBootCoinOrderByOperation_shouldReturnNullIfNotFound() {
        String operation = UUID.randomUUID().toString();

        when(hashOperations.get(REDIS_HASH, operation)).thenReturn(null);

        BootCoinOrder result = redisService.findAllBootCoinOrderByOperation(operation);
        assertNull(result);

        verify(hashOperations, times(1)).get(REDIS_HASH, operation);
    }

}
