package com.sgi.bootcoin.domain.model.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "bootcoin-order", timeToLive = 86400L)
public class BootCoinOrder implements Serializable {

    @Id
    @Indexed
    private String id;
    private String buyerId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private Instant createdAt;
}
