package com.sgi.bootcoin.infrastructure.subscriber.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountExistEvent {
    private String bootcoinId;
    private String accountId;
    private Boolean exist;

    public static final String TOPIC = "validation-account-response";
}
