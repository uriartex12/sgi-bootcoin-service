package com.sgi.bootcoin.infrastructure.mapper;

import com.sgi.bootcoin.domain.dto.UserDTO;
import com.sgi.bootcoin.infrastructure.enums.MovementType;
import com.sgi.bootcoin.infrastructure.subscriber.events.OrchestratorBootcoinEvent;
import com.sgi.bootcoin.domain.model.BootCoin;
import com.sgi.bootcoin.domain.model.redis.BootCoinOrder;
import com.sgi.bootcoin.infrastructure.dto.RateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static com.sgi.bootcoin.domain.shared.Constants.BOOTCOIN;

@Mapper
public interface ExternalOrchestratorDataMapper {

    ExternalOrchestratorDataMapper INSTANCE = Mappers.getMapper(ExternalOrchestratorDataMapper.class);

    default OrchestratorBootcoinEvent map(BootCoin bootCoin, BootCoinOrder bootCoinOrder,
                                          MovementType movement, BigDecimal amount,
                                          UserDTO userSend, UserDTO userReceiver,
                                          RateResponse rate, String paymentMethod) {

        return OrchestratorBootcoinEvent.builder()
                .bootcoinId(bootCoin.getId())
                .walletId(bootCoin.getYankiId())
                .accountId(bootCoin.getAccountId())
                .paymentMethod(paymentMethod)
                .type(movement.name())
                .currency(BOOTCOIN)
                .accountId(bootCoin.getAccountId())
                .amount(amount)
                .buyRate(rate.getBuyRate())
                .sellRate(rate.getSellRate())
                .operation(bootCoinOrder.getId())
                .sender(userSend)
                .receiver(userReceiver)
                .build();
    }
}