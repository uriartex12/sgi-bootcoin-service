package com.sgi.bootcoin.Infrastructure.subscriber.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletExistEvent {
    private String bootcoinId;
    private String yankiId;
    private Boolean exist;

    public static final String TOPIC = "validation-wallet-response";
}
