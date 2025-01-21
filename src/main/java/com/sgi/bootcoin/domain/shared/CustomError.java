package com.sgi.bootcoin.domain.shared;

import com.sgi.bootcoin.infrastructure.exception.ApiError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enum representing custom errors for the Card-service application.
 * Each constant includes an error code, message, and HTTP status for specific errors.
 */
@Getter
@AllArgsConstructor
public enum CustomError {

    E_OPERATION_FAILED(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "BOOTCOIN-000", "Operation failed")),
    E_INSUFFICIENT_BALANCE(new ApiError(HttpStatus.PAYMENT_REQUIRED, "BOOTCOIN-002", "Insufficient balance")),
    E_BOOTCOIN_NOT_FOUND(new ApiError(HttpStatus.NOT_FOUND, "BOOTCOIN-001", "Bootcoin not found")),
    E_PURCHASE_OPERATION_NOT_FOUND(new ApiError(HttpStatus.NOT_FOUND, "BOOTCOIN-003", "Operation number not found")),
    E_SELLER_ACCOUNT_NOT_ASSOCIATED(new ApiError(HttpStatus.NOT_FOUND, "BOOTCOIN-004", "You must provide a Yanki account or ID.")),
    E_TRANSACTION_NOT_PENDING(new ApiError(HttpStatus.BAD_REQUEST, "BOOTCOIN-005", "The transaction must be in 'PENDIENTE' state to proceed."));
    private final ApiError error;
}
