package com.sgi.bootcoin.infrastructure.subscriber.events;

import com.sgi.bootcoin.domain.dto.WalletDetailDTO;
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
    private WalletDetailDTO walletDetail;

    public static final String TOPIC = "validation-wallet-response";
}
