package com.sgi.bootcoin.Infrastructure.subscriber.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class YankiEvent {
    private String yankiId;
    private String bootcoinId;

    public static final String TOPIC = "validation-exists-yanki";
}
