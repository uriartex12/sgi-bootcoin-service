package com.sgi.bootcoin.application.service.impl;

import com.sgi.bootcoin.Infrastructure.subscriber.events.OrchestratorBootcoinEventResponse;
import com.sgi.bootcoin.domain.dto.UserDTO;
import com.sgi.bootcoin.Infrastructure.exception.CustomException;
import com.sgi.bootcoin.Infrastructure.mapper.BootcoinMapper;
import com.sgi.bootcoin.Infrastructure.mapper.ExternalOrchestratorDataMapper;
import com.sgi.bootcoin.Infrastructure.repository.BootcoinRepositoryJpa;
import com.sgi.bootcoin.Infrastructure.subscriber.events.BankAccountEvent;
import com.sgi.bootcoin.Infrastructure.subscriber.events.OrchestratorBootcoinEvent;
import com.sgi.bootcoin.Infrastructure.subscriber.events.YankiEvent;
import com.sgi.bootcoin.Infrastructure.subscriber.message.EventSender;
import com.sgi.bootcoin.application.service.RedisService;
import com.sgi.bootcoin.domain.model.BootCoin;
import com.sgi.bootcoin.domain.model.redis.BootCoinOrder;
import com.sgi.bootcoin.domain.port.BootcoinService;
import com.sgi.bootcoin.domain.port.ExchangeRateService;
import com.sgi.bootcoin.domain.shared.Constants;
import com.sgi.bootcoin.domain.shared.CustomError;
import com.sgi.bootcoin.infrastructure.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.sgi.bootcoin.Infrastructure.enums.MovementType.*;
import static com.sgi.bootcoin.Infrastructure.enums.paymentMethod.TRANSFER;
import static com.sgi.bootcoin.infrastructure.dto.PurchaseRequest.PaymentMethodEnum.YANKI;

@Service
@RequiredArgsConstructor
@Slf4j
public class BootcoinServiceImpl implements BootcoinService {

    private final BootcoinRepositoryJpa repositoryJpa;

    private final RedisService redisService;

    private final ExchangeRateService exchangeRateService;

    private final EventSender kafkaTemplate;

    @Override
    public Mono<BootcoinResponse> createBootcoin(Mono<BootcoinRequest> bootcoinRequestMono) {
        return bootcoinRequestMono
                .map(BootcoinMapper.INSTANCE::map)
                .flatMap(bootCoin -> {
                    bootCoin.setBootcoin(BigDecimal.ZERO);
                    return repositoryJpa.save(bootCoin);
                })
                .map(BootcoinMapper.INSTANCE::map);
    }

    @Override
    public Mono<Void> deleteBootcoin(String walletId) {
        return repositoryJpa.findById(walletId)
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_BOOTCOIN_NOT_FOUND)))
                .flatMap(repositoryJpa::delete);
    }

    @Override
    public Flux<BootcoinResponse> getAllBootcoin() {
        return repositoryJpa.findAll()
                .map(BootcoinMapper.INSTANCE::map);
    }

    @Override
    public Mono<BootcoinResponse> getBootcoinById(String walletId) {
        return repositoryJpa.findById(walletId)
                .map(BootcoinMapper.INSTANCE::map);
    }

    @Override
    public Mono<BootcoinResponse> updateBootcoin(String walletId, Mono<BootcoinRequest> bootcoinRequestMono) {
        return repositoryJpa.findById(walletId)
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_BOOTCOIN_NOT_FOUND)))
                .zipWith(bootcoinRequestMono, (bootCoin, bootcoinRequest) -> {
                    BootCoin updated = BootcoinMapper.INSTANCE.map(bootcoinRequest);
                    updated.setId(bootCoin.getId());
                    return updated;
                })
                .flatMap(repositoryJpa::save)
                .map(BootcoinMapper.INSTANCE::map);
    }

    @Override
    public Mono<BalanceResponse> getBootcoinBalance(String phone) {
        return repositoryJpa.findByPhone(phone)
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_BOOTCOIN_NOT_FOUND)))
                .flatMap(wallet -> Mono.just(BootcoinMapper.INSTANCE.toBalance(wallet)));
    }

    @Override
    public Mono<PurchaseResponse> createPurchaseBootcoin(Mono<PurchaseRequest> purchaseRequestMono) {
        return purchaseRequestMono.flatMap(purchaseRequest ->
                repositoryJpa.findById(purchaseRequest.getBuyerId())
                        .switchIfEmpty(Mono.error(new CustomException(CustomError.E_BOOTCOIN_NOT_FOUND)))
                        .flatMap(bootCoin -> {
                            BootCoinOrder bootCoinOrder = BootCoinOrder.builder()
                                    .id(Constants.generateRandomId())
                                    .paymentMethod(purchaseRequest.getPaymentMethod().name())
                                    .amount(purchaseRequest.getAmount())
                                    .buyerId(purchaseRequest.getBuyerId())
                                    .status(PurchaseResponse.StatusEnum.PENDING.name())
                                    .createdAt(Instant.now())
                                    .build();
                            return redisService.saveBootCoinOrder(bootCoinOrder.getId(), Mono.just(bootCoinOrder));
                        })
                        .map(BootcoinMapper.INSTANCE::toPurchaseResponse)
        );
    }

    @Override
    public Mono<AssociateResponse> associateWalletOrAccount(String action, String bootcoinId, Mono<AssociateRequest> associateRequest) {
        return repositoryJpa.findById(bootcoinId)
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_BOOTCOIN_NOT_FOUND)))
                .zipWith(associateRequest, (bootCoin, associate) -> {
                    if ("account".equals(action)) {
                        kafkaTemplate.sendEvent(BankAccountEvent.TOPIC, BankAccountEvent.builder()
                                .accountId(associate.getAccountId())
                                .bootcoinId(bootCoin.getId())
                                .build());
                    } else if ("yanki".equals(action)) {
                        kafkaTemplate.sendEvent(YankiEvent.TOPIC, YankiEvent.builder()
                                .yankiId(associate.getYankiId())
                                .bootcoinId(bootCoin.getId())
                                .build());
                    } else {
                        throw new IllegalArgumentException("Invalid action: " + action);
                    }
                    AssociateResponse associateResponse = new AssociateResponse();
                    associateResponse.setBootcoinId(bootcoinId);
                    associateResponse.setMessage(action.concat(" association in process"));
                    associateResponse.setTimestamp(OffsetDateTime.now());
                    return associateResponse;
                });
    }

    @Override
    public Mono<BootcoinResponse> associateBankAccount(String bootcoinId, String accountId) {
        return repositoryJpa.findById(bootcoinId)
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_BOOTCOIN_NOT_FOUND)))
                .flatMap(bootCoin -> {
                    bootCoin.setAccountId(accountId);
                    return repositoryJpa.save(bootCoin);
                }).map(BootcoinMapper.INSTANCE::map);
    }

    @Override
    public Mono<PurchaseResponse> acceptPurchaseBootcoin(String operation, Mono<AcceptPurchaseRequest> acceptPurchaseRequest) {
        return Mono.just(redisService.findAllBootCoinOrderByOperation(operation))
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_PURCHASE_OPERATION_NOT_FOUND)))
                .filter(purchaseOrder -> Objects.equals(purchaseOrder.getStatus(),
                        PurchaseResponse.StatusEnum.PENDING.name()))
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_TRANSACTION_NOT_PENDING)))
                .flatMap(bootCoinOrder ->
                        acceptPurchaseRequest.flatMap(acceptPurchase ->
                                exchangeRateService.getLastExchangeRate()
                                        .flatMap(rateResponse -> {
                                            BigDecimal bootCoinPurchase = this.calculatePurchaseBootcoin(bootCoinOrder.getAmount(), rateResponse.getBuyRate());
                                            BigDecimal amountSales = this.calculateSaleAmount(bootCoinPurchase, rateResponse.getSellRate());
                                            return validatedBootcoinSeller(acceptPurchase.getSellerId(), bootCoinPurchase)
                                                    .flatMap(seller ->
                                                            repositoryJpa.findById(bootCoinOrder.getBuyerId())
                                                                    .flatMap(buyer -> {
                                                                        buyer.setBootcoin(buyer.getBootcoin().add(bootCoinPurchase));
                                                                        seller.setBootcoin(seller.getBootcoin().subtract(bootCoinPurchase));
                                                                        return repositoryJpa.saveAll(Flux.just(buyer, seller))
                                                                                .last()
                                                                                .map(BootcoinMapper.INSTANCE::toPurchaseResponse)
                                                                                .doOnTerminate(() -> {
                                                                                    UserDTO buyerUser = createUserBuyerAndSeller(buyer);
                                                                                    UserDTO sellerUser = createUserBuyerAndSeller(seller);
                                                                                    List<OrchestratorBootcoinEvent> orchestratorBootcoinEvents = toMapperOrchestratorEvent(seller, buyer,
                                                                                            bootCoinOrder, rateResponse,
                                                                                            sellerUser, buyerUser, amountSales);

                                                                                    orchestratorBootcoinEvents.forEach(orchestratorWalletEvent -> {
                                                                                        kafkaTemplate.sendEvent(OrchestratorBootcoinEvent.TOPIC,
                                                                                                orchestratorWalletEvent);
                                                                                    });
                                                                                });
                                                                    })
                                                    );
                                        })

                        )
                );
    }

    @Override
    public Mono<BootcoinResponse> associateYanki(String bootcoinId, String yankiId) {
        return repositoryJpa.findById(bootcoinId)
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_BOOTCOIN_NOT_FOUND)))
                .flatMap(bootCoin -> {
                    bootCoin.setYankiId(yankiId);
                    return repositoryJpa.save(bootCoin);
                }).map(BootcoinMapper.INSTANCE::map);
    }

    @Override
    public Mono<BootcoinResponse> invalidatePurchaseProcess(OrchestratorBootcoinEventResponse bootcoinReverse) {
        return repositoryJpa.findById(bootcoinReverse.getBootcoinId())
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_BOOTCOIN_NOT_FOUND)))
                .flatMap(bootCoin -> {
                    BigDecimal adjustedBootcoin = calculatePurchaseBootcoin(
                            bootcoinReverse.getAmount(),
                            PURCHASE.name().equals(bootcoinReverse.getType())
                                    ? bootcoinReverse.getBuyRate()
                                    : bootcoinReverse.getSellRate()
                    );
                    if (PURCHASE.name().equals(bootcoinReverse.getType())) {
                        bootCoin.setBootcoin(bootCoin.getBootcoin().subtract(adjustedBootcoin));
                    } else if (SALE.name().equals(bootcoinReverse.getType())) {
                        bootCoin.setBootcoin(bootCoin.getBootcoin().add(adjustedBootcoin));
                    } else {
                        return Mono.error(new CustomException(CustomError.E_OPERATION_FAILED));
                    }
                    return repositoryJpa.save(bootCoin);
                })
                .map(BootcoinMapper.INSTANCE::map);
    }

    private List<OrchestratorBootcoinEvent> toMapperOrchestratorEvent(BootCoin seller, BootCoin buyer, BootCoinOrder bootCoinOrder,
                                                                      RateResponse rateResponse, UserDTO sellerUser, UserDTO buyerUser,
                                                                      BigDecimal amountSales){
        return List.of(
              ExternalOrchestratorDataMapper.INSTANCE.map(buyer,
                      bootCoinOrder, PURCHASE,
                      bootCoinOrder.getAmount(),
                      buyerUser, sellerUser,
                      rateResponse, bootCoinOrder.getPaymentMethod()),

             ExternalOrchestratorDataMapper.INSTANCE.map(seller,
                     bootCoinOrder, SALE,
                     amountSales, sellerUser,
                     buyerUser,
                     rateResponse,
                     Optional.ofNullable(seller.getAccountId())
                     .map(accountId -> TRANSFER.name())
                     .orElseGet(() ->
                             Optional.ofNullable(seller.getYankiId())
                                 .map(yankiId -> YANKI.name())
                                 .orElseThrow(() ->
                                     new CustomException(CustomError.E_SELLER_ACCOUNT_NOT_ASSOCIATED)
                             )
                     ))
        );
    }

    private Mono<BootCoin> validatedBootcoinSeller(String sellerId, BigDecimal bootCoinPurchase){
      return repositoryJpa.findById(sellerId)
                .filter(x -> x.getBootcoin().compareTo(bootCoinPurchase) >= 0)
                .switchIfEmpty(Mono.error(new CustomException(CustomError.E_INSUFFICIENT_BALANCE)));
    }

    public BigDecimal calculatePurchaseBootcoin(BigDecimal amountInSoles, BigDecimal buyRate) {
        return amountInSoles.divide(buyRate, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateSaleAmount(BigDecimal amountInBootCoin, BigDecimal sellRate) {
        return amountInBootCoin.multiply(sellRate)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private UserDTO createUserBuyerAndSeller(BootCoin user) {
        return UserDTO.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .phone(user.getPhone())
                    .dni(user.getDocumentNumber())
                .build();
    }

}
