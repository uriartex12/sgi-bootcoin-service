package com.sgi.bootcoin.infrastructure.subscriber.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BankAccountEvent {
    private String accountId;
    private String bootcoinId;

    public static final String TOPIC = "validation-exists-account";
}
