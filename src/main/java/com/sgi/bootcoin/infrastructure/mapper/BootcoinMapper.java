package com.sgi.bootcoin.infrastructure.mapper;

import com.sgi.bootcoin.domain.model.BootCoin;
import com.sgi.bootcoin.domain.model.redis.BootCoinOrder;
import com.sgi.bootcoin.infrastructure.dto.BalanceResponse;
import com.sgi.bootcoin.infrastructure.dto.BootcoinRequest;
import com.sgi.bootcoin.infrastructure.dto.BootcoinResponse;
import com.sgi.bootcoin.infrastructure.dto.PurchaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Mapper
public interface BootcoinMapper {

    BootcoinMapper INSTANCE = Mappers.getMapper(BootcoinMapper.class);

    BootCoin map(BootcoinRequest bootcoinRequest);

    BootcoinResponse map(BootCoin bootCoin);

    BalanceResponse toBalance(BootCoin wallet);

    @Mapping(target = "operation", source = "bootCoinOrder.id")
    PurchaseResponse toPurchaseResponse(BootCoinOrder bootCoinOrder);

    default PurchaseResponse toPurchaseResponse(BootCoin bootCoin, BigDecimal amount, String operation){
        PurchaseResponse purchaseResponse = new PurchaseResponse();
        purchaseResponse.setAmount(amount);
        purchaseResponse.setOperation(operation);
        purchaseResponse.setStatus(PurchaseResponse.StatusEnum.ACCEPTED);
        purchaseResponse.setTimestamp(OffsetDateTime.now());
        purchaseResponse.setBuyerId(bootCoin.getId());
        return purchaseResponse;
    }
}
