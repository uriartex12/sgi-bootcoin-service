package com.sgi.bootcoin.Infrastructure.mapper;

import com.sgi.bootcoin.domain.model.BootCoin;
import com.sgi.bootcoin.domain.model.redis.BootCoinOrder;
import com.sgi.bootcoin.infrastructure.dto.BalanceResponse;
import com.sgi.bootcoin.infrastructure.dto.BootcoinRequest;
import com.sgi.bootcoin.infrastructure.dto.BootcoinResponse;
import com.sgi.bootcoin.infrastructure.dto.PurchaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BootcoinMapper {

    BootcoinMapper INSTANCE = Mappers.getMapper(BootcoinMapper.class);

    BootCoin map(BootcoinRequest bootcoinRequest);

    BootcoinResponse map(BootCoin bootCoin);

    BalanceResponse toBalance(BootCoin wallet);

    @Mapping(target = "operation", source = "bootCoinOrder.id")
    PurchaseResponse toPurchaseResponse(BootCoinOrder bootCoinOrder);

    PurchaseResponse toPurchaseResponse(BootCoin bootCoin);
}
