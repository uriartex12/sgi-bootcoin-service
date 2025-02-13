package com.sgi.bootcoin.domain.model;

import com.sgi.bootcoin.domain.dto.WalletDetailDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "bootcoin")
@CompoundIndex(def = "{'id': 1, 'phone': 1, 'documentNumber': 1}", name = "id_phone_documentNumber_index", unique = true)
public class BootCoin {

    @Id
    private String id;
    private String name;
    @NotNull(message = "Phone number is required")
    private String phone;
    private String email;
    private String documentNumber;
    private String documentType;
    private String accountId;
    private String yankiId;
    private WalletDetailDTO yankiDetail;
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal bootcoin;

}
