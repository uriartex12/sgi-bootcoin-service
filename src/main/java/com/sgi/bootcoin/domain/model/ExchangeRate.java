package com.sgi.bootcoin.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Document( collection = "rate")
public class ExchangeRate {

    @Id
    private String id;
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal buyRate;
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal sellRate;
    @CreatedDate
    private Instant createdDate;
    @LastModifiedDate
    private Instant updatedDate;

}
