package com.sgi.bootcoin.infrastructure.mapper;

import com.sgi.bootcoin.domain.model.ExchangeRate;
import com.sgi.bootcoin.infrastructure.dto.RateRequest;
import com.sgi.bootcoin.infrastructure.dto.RateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper
public interface ExchangeRateMapper {

    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedDate", expression = "java(java.time.Instant.now())")
    ExchangeRate map(RateRequest rateRequest);

    RateResponse map(ExchangeRate exchangeRate);

    default OffsetDateTime map(Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }
}
