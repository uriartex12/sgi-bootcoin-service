package com.sgi.bootcoin.Infrastructure.controller;

import com.sgi.bootcoin.domain.port.BootcoinService;
import com.sgi.bootcoin.domain.port.ExchangeRateService;
import com.sgi.bootcoin.infrastructure.controller.V1Api;
import com.sgi.bootcoin.infrastructure.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class BootcoinController implements V1Api {

    private final ExchangeRateService exchangeRateService;

    private final BootcoinService bootcoinService;

    @Override
    public Mono<ResponseEntity<PurchaseResponse>> acceptPurchaseBootcoin(String operation, Mono<AcceptPurchaseRequest> acceptPurchaseRequest, ServerWebExchange exchange) {
        return bootcoinService.acceptPurchaseBootcoin(operation, acceptPurchaseRequest)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<AssociateResponse>> associateWalletOrAccount(String action, String bootcoinId, Mono<AssociateRequest> associateRequest, ServerWebExchange exchange) {
        return bootcoinService.associateWalletOrAccount(action, bootcoinId, associateRequest)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<BootcoinResponse>> createBootcoin(Mono<BootcoinRequest> bootcoinRequest, ServerWebExchange exchange) {
        return bootcoinService.createBootcoin(bootcoinRequest)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<RateResponse>> createExchangeRate(Mono<RateRequest> rateRequest, ServerWebExchange exchange) {
        return exchangeRateService.createExchangeRate(rateRequest)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<PurchaseResponse>> createPurchaseBootcoin(Mono<PurchaseRequest> purchaseRequest, ServerWebExchange exchange) {
        return bootcoinService.createPurchaseBootcoin(purchaseRequest)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteBootcoin(String bootcoinId, ServerWebExchange exchange) {
        return bootcoinService.deleteBootcoin(bootcoinId)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<BootcoinResponse>>> getAllBootcoin(ServerWebExchange exchange) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(bootcoinService.getAllBootcoin()));
    }

    @Override
    public Mono<ResponseEntity<Flux<RateResponse>>> getAllExchangeRate(ServerWebExchange exchange) {
        return Mono.fromSupplier(() -> ResponseEntity.ok(exchangeRateService.getAllExchangeRate()));
    }

    @Override
    public Mono<ResponseEntity<BalanceResponse>> getBootcoinBalance(String phone, ServerWebExchange exchange) {
        return bootcoinService.getBootcoinBalance(phone)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<BootcoinResponse>> getBootcoinById(String bootcoinId, ServerWebExchange exchange) {
        return bootcoinService.getBootcoinById(bootcoinId)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<BootcoinResponse>> updateBootcoin(String bootcoinId, Mono<BootcoinRequest> bootcoinRequest, ServerWebExchange exchange) {
        return bootcoinService.updateBootcoin(bootcoinId, bootcoinRequest)
                .map(ResponseEntity::ok);
    }
}
