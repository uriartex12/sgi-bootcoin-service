package com.sgi.bootcoin.helper;

import com.sgi.bootcoin.infrastructure.subscriber.events.BankAccountExistEvent;
import com.sgi.bootcoin.infrastructure.subscriber.events.OrchestratorBootcoinEventResponse;
import com.sgi.bootcoin.infrastructure.subscriber.events.WalletExistEvent;
import com.sgi.bootcoin.domain.dto.WalletDetailDTO;
import com.sgi.bootcoin.domain.model.BootCoin;
import com.sgi.bootcoin.domain.model.redis.BootCoinOrder;
import com.sgi.bootcoin.infrastructure.dto.*;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static com.sgi.bootcoin.infrastructure.dto.PurchaseRequest.PaymentMethodEnum.YANKI;

public class FactoryTest {

    @SneakyThrows
    public static <R> R toFactoryBootcoin(Class<R> response) {
        R account = response.getDeclaredConstructor().newInstance();
        if (account instanceof BootcoinRequest bootcoinRequest) {
            return (R) initializeBootcoin(bootcoinRequest);
        } else if (account instanceof BootcoinResponse bootcoinResponse) {
            return (R) initializeBootcoin(bootcoinResponse);
        }
        return account;
    }

    private static BootcoinRequest initializeBootcoin(BootcoinRequest bootcoin) {
        bootcoin.setDocumentNumber("712338232");
        bootcoin.setEmail("test@gmail.com");
        bootcoin.setName("test-bootcoin");
        bootcoin.setPhone("910672362");
        bootcoin.setDocumentType("DNI");
        return bootcoin;
    }

    private static BootcoinResponse initializeBootcoin(BootcoinResponse bootcoin) {
        bootcoin.id(UUID.randomUUID().toString());
        bootcoin.setDocumentNumber("712338232");
        bootcoin.setEmail("test@gmail.com");
        bootcoin.setName("test-bootcoin");
        bootcoin.setPhone("910672362");
        bootcoin.setDocumentType("DNI");
        bootcoin.bootcoin(BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP));
        return bootcoin;
    }

    public static List<BootcoinResponse> toFactoryListBootcoinResponse() {
        BootcoinResponse bootcoin = new BootcoinResponse();
        bootcoin.id(UUID.randomUUID().toString());
        bootcoin.setDocumentNumber("712338232");
        bootcoin.setEmail("test@gmail.com");
        bootcoin.setName("test-bootcoin");
        bootcoin.setPhone("910672362");
        bootcoin.setDocumentType("DNI");
        return List.of(bootcoin);
    }

    public static BootCoin toFactoryBootcoin(BootcoinRequest bootcoinRequest) {
        return BootCoin.builder()
                .id(UUID.randomUUID().toString())
                .email(bootcoinRequest.getEmail())
                .phone(bootcoinRequest.getPhone())
                .name(bootcoinRequest.getName())
                .documentNumber(bootcoinRequest.getDocumentNumber())
                .documentType(bootcoinRequest.getDocumentType())
                .bootcoin(BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP))
                .yankiId(UUID.randomUUID().toString())
                .yankiDetail(WalletDetailDTO.builder()
                        .accountId(UUID.randomUUID().toString())
                        .type("DEBIT")
                        .clientId(UUID.randomUUID().toString())
                        .build())
                .build();
    }

    public static List<BootCoin> toFactoryListBootcoin() {
        BootCoin bootCoin = BootCoin.builder()
                .id(UUID.randomUUID().toString())
                .email("test@gmail.com")
                .phone("910672362")
                .name("test-bootcoin")
                .documentNumber("712338232")
                .documentType("DNI")
                .build();
        return List.of(bootCoin);
    }

    @SneakyThrows
    public static <R> R toFactoryExchangeRate(Class<R> response) {
        R account = response.getDeclaredConstructor().newInstance();
        if (account instanceof RateRequest rateRequest) {
            return (R) initializeRate(rateRequest);
        } else if (account instanceof RateResponse rateResponse) {
            return (R) initializeRate(rateResponse);
        }
        return account;
    }

    private static RateRequest initializeRate(RateRequest rate) {
        rate.setSellRate(BigDecimal.ZERO);
        rate.setBuyRate(BigDecimal.ONE);
        return rate;
    }

    private static RateResponse initializeRate(RateResponse rate) {
        rate.setId(UUID.randomUUID().toString());
        rate.setSellRate(BigDecimal.ZERO);
        rate.setBuyRate(BigDecimal.ONE);
        return rate;
    }

    public static List<RateResponse> toFactoryListRateResponse() {
        RateResponse rate = new RateResponse();
        rate.setSellRate(BigDecimal.ZERO);
        rate.setBuyRate(BigDecimal.ONE);
        return List.of(rate);
    }

    public static BalanceResponse toFactoryBalance(String phone) {
        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setBalance(BigDecimal.TEN);
        balanceResponse.setPhone(phone);
        balanceResponse.setBootcoinId(UUID.randomUUID().toString());
        return  balanceResponse;
    }

    @SneakyThrows
    public static <R> R toFactoryPurchase(Class<R> response) {
        R account = response.getDeclaredConstructor().newInstance();
        if (account instanceof PurchaseRequest purchaseRequest) {
            return (R) initializePurchase(purchaseRequest);
        } else if (account instanceof PurchaseResponse purchaseResponse) {
            return (R) initializePurchase(purchaseResponse);
        }
        return account;
    }


    public static PurchaseRequest initializePurchase(PurchaseRequest purchaseRequest) {
        purchaseRequest.setBuyerId(UUID.randomUUID().toString());
        purchaseRequest.setPaymentMethod(YANKI);
        purchaseRequest.setAmount(BigDecimal.TEN);
        return purchaseRequest;
    }
    public static PurchaseResponse initializePurchase(PurchaseResponse purchaseResponse) {
        purchaseResponse.setStatus(PurchaseResponse.StatusEnum.PENDING);
        purchaseResponse.setAmount(BigDecimal.TEN);
        purchaseResponse.setOperation(UUID.randomUUID().toString());
        purchaseResponse.setTimestamp(OffsetDateTime.now());
        return purchaseResponse;
    }

    public static AssociateRequest toFactoryAssociateRequest() {
        AssociateRequest associateRequest = new AssociateRequest();
        associateRequest.setAccountId(UUID.randomUUID().toString());
        associateRequest.setYankiId(UUID.randomUUID().toString());
        return associateRequest;
    }

    public static AssociateResponse toFactoryAssociateResponse() {
        AssociateResponse associateResponse = new AssociateResponse();
        associateResponse.setMessage("Proceso en ejecucion");
        associateResponse.setBootcoinId(UUID.randomUUID().toString());
        associateResponse.setTimestamp(OffsetDateTime.now());
        return associateResponse;
    }

    public static AcceptPurchaseRequest toFactoryAcceptPurchase() {
        AcceptPurchaseRequest acceptPurchaseRequest = new AcceptPurchaseRequest();
        acceptPurchaseRequest.setSellerId(UUID.randomUUID().toString());
        return  acceptPurchaseRequest;
    }

    public static BootCoinOrder toFactoryBootcoinOrder() {
        BootCoinOrder bootCoinOrder = new BootCoinOrder();
        bootCoinOrder.setStatus(PurchaseResponse.StatusEnum.PENDING.name());
        bootCoinOrder.setPaymentMethod(YANKI.name());
        bootCoinOrder.setAmount(BigDecimal.TEN);
        bootCoinOrder.setBuyerId(UUID.randomUUID().toString());
        bootCoinOrder.setCreatedAt(Instant.now());

        return bootCoinOrder;
    }


    public static OrchestratorBootcoinEventResponse toFactoryOrchestratorBootcoin() {
        return OrchestratorBootcoinEventResponse.builder()
                .bootcoinId(UUID.randomUUID().toString())
                .amount(BigDecimal.ONE)
                .accountId(UUID.randomUUID().toString())
                .buyRate(BigDecimal.valueOf(1.3))
                .status("COMPLETED")
                .sellRate(BigDecimal.valueOf(4.3))
                .build();
    }

    public static BigDecimal calculatePurchaseBootcoin(BigDecimal amountInSoles, BigDecimal buyRate) {
        return amountInSoles.divide(buyRate, 2, RoundingMode.HALF_UP);
    }
    public static BigDecimal calculateSaleAmount(BigDecimal amountInBootCoin, BigDecimal sellRate) {
        return amountInBootCoin.multiply(sellRate)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static BankAccountExistEvent toFactoryBankAccountExist() {

        return BankAccountExistEvent.builder()
                .exist(true)
                .accountId(UUID.randomUUID().toString())
                .bootcoinId(UUID.randomUUID().toString())
                .build();
    }

    public static WalletExistEvent toFactoryWalletExistEvent() {
        return WalletExistEvent.builder()
                .exist(true)
                .yankiId(UUID.randomUUID().toString())
                .walletDetail(WalletDetailDTO.builder()
                        .clientId(UUID.randomUUID().toString())
                        .accountId(UUID.randomUUID().toString())
                        .build())
                .bootcoinId(UUID.randomUUID().toString())
                .build();
    }


}
