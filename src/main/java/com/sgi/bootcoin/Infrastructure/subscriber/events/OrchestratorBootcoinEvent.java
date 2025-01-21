package com.sgi.bootcoin.Infrastructure.subscriber.events;

import com.sgi.bootcoin.domain.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrchestratorBootcoinEvent implements EventHandle {
    private String bootcoinId;
    private String walletId;
    private String accountId;
    private String type;
    private BigDecimal amount;
    private String paymentMethod;
    private String currency;
    private BigDecimal buyRate;
    private BigDecimal sellRate;
    private String description;
    private String operation;
    private UserDTO sender;
    private UserDTO receiver;

    public static final String TOPIC = "OrchestratorBootcoinEvent";
}
