package com.sgi.bootcoin.domain.port;

import com.sgi.bootcoin.infrastructure.subscriber.events.OrchestratorBootcoinEventResponse;
import com.sgi.bootcoin.domain.dto.WalletDetailDTO;
import com.sgi.bootcoin.infrastructure.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcoinService {
    Mono<BootcoinResponse> createBootcoin(Mono<BootcoinRequest> bootcoinRequestMono);
    Mono<Void> deleteBootcoin(String walletId);
    Flux<BootcoinResponse> getAllBootcoin();
    Mono<BootcoinResponse> getBootcoinById(String walletId);
    Mono<BootcoinResponse> updateBootcoin(String walletId, Mono<BootcoinRequest> bootcoinRequestMono);
    Mono<BalanceResponse> getBootcoinBalance(String phone);
    Mono<PurchaseResponse> createPurchaseBootcoin(Mono<PurchaseRequest> purchaseRequest);
    Mono<AssociateResponse> associateWalletOrAccount(String action, String bootcoinId, Mono<AssociateRequest> associateRequest);
    Mono<BootcoinResponse> associateYanki(String bootcoinId, String yankiId, WalletDetailDTO walletDetail);
    Mono<BootcoinResponse> associateBankAccount(String bootcoinId, String accountId);
    Mono<PurchaseResponse> acceptPurchaseBootcoin(String operation, Mono<AcceptPurchaseRequest> acceptPurchaseRequest);
    Mono<BootcoinResponse> invalidatePurchaseProcess(OrchestratorBootcoinEventResponse bootcoinReverse);
}
