package com.sgi.bootcoin.appliction.service;

import com.sgi.bootcoin.infrastructure.exception.CustomException;
import com.sgi.bootcoin.infrastructure.mapper.BootcoinMapper;
import com.sgi.bootcoin.infrastructure.repository.BootcoinRepositoryJpa;
import com.sgi.bootcoin.infrastructure.subscriber.events.OrchestratorBootcoinEventResponse;
import com.sgi.bootcoin.infrastructure.subscriber.message.EventSender;
import com.sgi.bootcoin.application.service.RedisService;
import com.sgi.bootcoin.application.service.impl.BootcoinServiceImpl;
import com.sgi.bootcoin.application.service.impl.ExchangeRateServiceImpl;
import com.sgi.bootcoin.domain.model.BootCoin;
import com.sgi.bootcoin.domain.model.redis.BootCoinOrder;
import com.sgi.bootcoin.helper.FactoryTest;
import com.sgi.bootcoin.infrastructure.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit test class for the BootcoinServiceImpl service.
 * This class contains unit tests for the methods of the BootcoinServiceImpl class,
 * ensuring that bootcoin are managed correctly, data is validated,
 * and potential errors are handled appropriately.
 */
@ExtendWith(MockitoExtension.class)
public class BootcoinServiceImplTest {

    @Mock
    private BootcoinRepositoryJpa bootcoinRepositoryJpa;

    @InjectMocks
    private BootcoinServiceImpl bootcoinService;

    @Mock
    private RedisService redisService;

    @Mock
    private EventSender kafkaTemplate;

    @Mock
    private ExchangeRateServiceImpl exchangeRateService;


    @Test
    void createBootcoin_shouldReturnCreatedResponse() {
        BootcoinRequest bootcoinRequest = FactoryTest.toFactoryBootcoin(BootcoinRequest.class);
        BootCoin bootcoin = FactoryTest.toFactoryBootcoin(bootcoinRequest);
        BootcoinResponse bootcoinResponse = BootcoinMapper.INSTANCE.map(bootcoin);
        when(bootcoinRepositoryJpa.save(any(BootCoin.class))).thenReturn(Mono.just(bootcoin));
        Mono<BootcoinResponse> result = bootcoinService.createBootcoin(Mono.just(bootcoinRequest));
        StepVerifier.create(result)
                .expectNext(bootcoinResponse)
                .verifyComplete();

        verify(bootcoinRepositoryJpa, times(1)).save(any(BootCoin.class));
    }

    @Test
    void deleteBootcoin_shouldReturnVoid() {
        String bootcoinId = UUID.randomUUID().toString();
        BootcoinRequest bootcoinRequest = FactoryTest.toFactoryBootcoin(BootcoinRequest.class);
        BootCoin bootcoin = FactoryTest.toFactoryBootcoin(bootcoinRequest);
        bootcoin.setId(bootcoinId);
        when(bootcoinRepositoryJpa.findById(bootcoinId)).thenReturn(Mono.just(bootcoin));
        when(bootcoinRepositoryJpa.delete(bootcoin)).thenReturn(Mono.empty());
        Mono<Void> result = bootcoinService.deleteBootcoin(bootcoinId);
        StepVerifier.create(result)
                .verifyComplete();
        verify(bootcoinRepositoryJpa).findById(bootcoinId);
        verify(bootcoinRepositoryJpa).delete(bootcoin);
    }

    @Test
    void deleteBootcoin_shouldReturnNotFound() {
        String bootcoinId = UUID.randomUUID().toString();
        when(bootcoinRepositoryJpa.findById(bootcoinId)).thenReturn(Mono.empty());
        Mono<Void> result = bootcoinService.deleteBootcoin(bootcoinId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof CustomException
                        &&
                        throwable.getMessage().equals("Bootcoin not found"))
                .verify();
        verify(bootcoinRepositoryJpa).findById(bootcoinId);
        verify(bootcoinRepositoryJpa, never()).delete(any());
    }

    @Test
    void getAllBootcoin_shouldReturnListBootcoinResponse() {
        List<BootCoin> bootcoinResponseList = FactoryTest.toFactoryListBootcoin();
        when(bootcoinRepositoryJpa.findAll()).thenReturn(Flux.fromIterable(bootcoinResponseList));
        Flux<BootcoinResponse> result = bootcoinService.getAllBootcoin();

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
        verify(bootcoinRepositoryJpa, times(1)).findAll();
    }

    @Test
    void getBootcoinById_shouldReturnListTransactionResponse() {
        String bootcoinId =  UUID.randomUUID().toString();
        BootcoinRequest bootcoinRequest = FactoryTest.toFactoryBootcoin(BootcoinRequest.class);
        BootCoin bootcoin = FactoryTest.toFactoryBootcoin(bootcoinRequest);
        bootcoin.setId(bootcoinId);
        when(bootcoinRepositoryJpa.findById(bootcoinId)).thenReturn(Mono.just(bootcoin));
        Mono<BootcoinResponse> result = bootcoinService.getBootcoinById(bootcoinId);
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
        verify(bootcoinRepositoryJpa, times(1)).findById(bootcoinId);
    }

    @Test
    void getBootcoinBalance_shouldReturnBalanceResponse(){
        String phone =  UUID.randomUUID().toString();
        BootcoinRequest bootcoinRequest = FactoryTest.toFactoryBootcoin(BootcoinRequest.class);
        BootCoin bootcoin = FactoryTest.toFactoryBootcoin(bootcoinRequest);
        when(bootcoinRepositoryJpa.findByPhone(phone)).thenReturn(Mono.just(bootcoin));

        Mono<BalanceResponse> result = bootcoinService.getBootcoinBalance(phone);

        StepVerifier.create(result)
                .expectNext(BootcoinMapper.INSTANCE.toBalance(bootcoin))
                .verifyComplete();
        verify(bootcoinRepositoryJpa, times(1)).findByPhone(phone);
    }


    @Test
    void createPurchaseBootcoin_shouldReturnCreatedResponse() {
        BootcoinRequest bootcoinRequest = FactoryTest.toFactoryBootcoin(BootcoinRequest.class);
        PurchaseRequest purchaseRequest = FactoryTest.toFactoryPurchase(PurchaseRequest.class);
        BootCoinOrder bootCoinOrder = FactoryTest.toFactoryBootcoinOrder();
        PurchaseResponse purchaseResponse = BootcoinMapper.INSTANCE.toPurchaseResponse(bootCoinOrder);
        BootCoin bootcoin = FactoryTest.toFactoryBootcoin(bootcoinRequest);

        when(bootcoinRepositoryJpa.findById(purchaseRequest.getBuyerId())).thenReturn(Mono.just(bootcoin));

        when(redisService.saveBootCoinOrder(anyString(), any(Mono.class))).thenReturn(Mono.just(bootCoinOrder));

        Mono<PurchaseResponse> result = bootcoinService.createPurchaseBootcoin(Mono.just(purchaseRequest));
        StepVerifier.create(result)
                .expectNext(purchaseResponse)
                .verifyComplete();

        verify(bootcoinRepositoryJpa, times(1)).findById(purchaseRequest.getBuyerId());
    }


    private static Stream<Arguments> actionProvider() {
        return Stream.of(
                Arguments.of("account", true),
                Arguments.of("yanki", true),
                Arguments.of("invalid", false)
        );
    }
    @ParameterizedTest
    @MethodSource("actionProvider")
    public void testAssociateWalletOrAccount(String action, boolean isValidAction) {
        String bootcoinId = UUID.randomUUID().toString();
        AssociateRequest associateRequest = FactoryTest.toFactoryAssociateRequest();
        BootcoinRequest bootcoinRequest = FactoryTest.toFactoryBootcoin(BootcoinRequest.class);
        BootCoin bootcoin = FactoryTest.toFactoryBootcoin(bootcoinRequest);

        when(bootcoinRepositoryJpa.findById(bootcoinId)).thenReturn(Mono.just(bootcoin));
        Mono<AssociateResponse> responseMono = bootcoinService.associateWalletOrAccount(action, bootcoinId, Mono.just(associateRequest));
        if (isValidAction) {
            StepVerifier.create(responseMono)
                    .expectNextMatches(response -> response.getMessage().contains("association in process"))
                    .expectComplete()
                    .verify();
        } else {
            StepVerifier.create(responseMono)
                    .verifyErrorMatches(throwable -> true);
        }
    }

    @Test
    public void testAssociateBankAccount_Success() {
        String bootcoinId = UUID.randomUUID().toString();
        String accountId = UUID.randomUUID().toString();
        BootcoinRequest bootcoinRequest = FactoryTest.toFactoryBootcoin(BootcoinRequest.class);
        BootCoin bootcoin = FactoryTest.toFactoryBootcoin(bootcoinRequest);
        bootcoin.setId(bootcoinId);
        BootcoinResponse bootcoinResponse = FactoryTest.toFactoryBootcoin(BootcoinResponse.class);
        bootcoinResponse.setId(bootcoinId);
        bootcoinResponse.setAccountId(accountId);
        bootcoinResponse.yankiId(bootcoin.getYankiId());

        when(bootcoinRepositoryJpa.findById(bootcoin.getId())).thenReturn(Mono.just(bootcoin));
        when(bootcoinRepositoryJpa.save(bootcoin)).thenReturn(Mono.just(bootcoin));

        Mono<BootcoinResponse> responseMono = bootcoinService.associateBankAccount(bootcoinId, accountId);

        StepVerifier.create(responseMono)
                .expectNext(bootcoinResponse)
                .expectComplete()
                .verify();
        verify(bootcoinRepositoryJpa).findById(bootcoinId);
        verify(bootcoinRepositoryJpa).save(bootcoin);
    }

    @Test
    public void testAssociateBankAccount_BootcoinNotFound() {
        String bootcoinId = "bootcoinId123";
        String accountId = "accountId123";
        when(bootcoinRepositoryJpa.findById(bootcoinId)).thenReturn(Mono.empty());
        Mono<BootcoinResponse> responseMono = bootcoinService.associateBankAccount(bootcoinId, accountId);

        StepVerifier.create(responseMono)
                .verifyError(CustomException.class);

        verify(bootcoinRepositoryJpa).findById(bootcoinId);
    }


    @ParameterizedTest
    @ValueSource(strings = {"PURCHASE", "SALE"})
    public void testInvalidatePurchaseProcess(String type) {
        String bootcoinId = UUID.randomUUID().toString();
        OrchestratorBootcoinEventResponse eventResponse = FactoryTest.toFactoryOrchestratorBootcoin();
        eventResponse.setBootcoinId(bootcoinId);
        eventResponse.setType(type);
        BootcoinRequest bootcoinRequest = FactoryTest.toFactoryBootcoin(BootcoinRequest.class);
        BootCoin bootcoin = FactoryTest.toFactoryBootcoin(bootcoinRequest);
        bootcoin.setId(bootcoinId);
        BootcoinResponse bootcoinResponse = BootcoinMapper.INSTANCE.map(bootcoin);
        if ("PURCHASE".equals(type)) {
            bootcoinResponse.setBootcoin(bootcoinResponse.getBootcoin()
                    .subtract(FactoryTest.calculatePurchaseBootcoin(eventResponse.getAmount(),
                            eventResponse.getBuyRate())));
        }else{
            bootcoinResponse.setBootcoin(bootcoinResponse.getBootcoin()
                    .add(FactoryTest.calculatePurchaseBootcoin(eventResponse.getAmount(),
                            eventResponse.getSellRate())));
        }
        when(bootcoinRepositoryJpa.findById(bootcoin.getId())).thenReturn(Mono.just(bootcoin));
        when(bootcoinRepositoryJpa.save(any(BootCoin.class))).thenReturn(Mono.just(bootcoin));

        Mono<BootcoinResponse> responseMono = bootcoinService.invalidatePurchaseProcess(eventResponse);

        StepVerifier.create(responseMono)
                .expectNext(bootcoinResponse)
                .expectComplete()
                .verify();

        verify(bootcoinRepositoryJpa).findById(bootcoinId);

        verify(bootcoinRepositoryJpa).save(bootcoin);
    }


    @Test
    public void testAcceptPurchaseBootcoin_success() {
        String operation = UUID.randomUUID().toString();
        BootcoinRequest bootcoinRequest = FactoryTest.toFactoryBootcoin(BootcoinRequest.class);
        BootCoin bootCoinSeller = FactoryTest.toFactoryBootcoin(bootcoinRequest);
        BootCoin bootCoinBuyer = FactoryTest.toFactoryBootcoin(bootcoinRequest);
        AcceptPurchaseRequest acceptPurchaseRequest = FactoryTest.toFactoryAcceptPurchase();
        BootCoinOrder bootCoinOrder = FactoryTest.toFactoryBootcoinOrder();
        RateResponse rateResponse = FactoryTest.toFactoryExchangeRate(RateResponse.class);
        BigDecimal bootCoinPurchase = FactoryTest.calculatePurchaseBootcoin(bootCoinOrder.getAmount(), rateResponse.getBuyRate());
        BigDecimal amountSales = FactoryTest.calculateSaleAmount(bootCoinPurchase, rateResponse.getSellRate());
        PurchaseResponse purchaseResponse = BootcoinMapper.INSTANCE.toPurchaseResponse(bootCoinSeller, amountSales, operation);

        when(redisService.findAllBootCoinOrderByOperation(anyString())).thenReturn(bootCoinOrder);
        when(bootcoinRepositoryJpa.findById(anyString())).thenReturn(Mono.just(bootCoinSeller));
        when(exchangeRateService.getLastExchangeRate()).thenReturn(Mono.just(rateResponse));
        when(bootcoinRepositoryJpa.findById(anyString())).thenReturn(Mono.just(bootCoinBuyer));
        when(bootcoinRepositoryJpa.saveAll(any(Flux.class))).thenReturn(Flux.just(bootCoinBuyer, bootCoinSeller));


        Mono<PurchaseResponse> responseMono = bootcoinService.acceptPurchaseBootcoin(operation, Mono.just(acceptPurchaseRequest));

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(purchaseResponse.getBuyerId(), response.getBuyerId());
                    assertEquals(purchaseResponse.getPaymentMethod(), response.getPaymentMethod());
                    assertEquals(purchaseResponse.getAmount(), response.getAmount());
                    assertEquals(purchaseResponse.getStatus(), response.getStatus());
                    assertEquals(purchaseResponse.getOperation(), response.getOperation());
                })
                .expectComplete()
                .verify();

        verify(redisService, times(1)).findAllBootCoinOrderByOperation(anyString());
        verify(bootcoinRepositoryJpa, times(2)).findById(anyString());
        verify(bootcoinRepositoryJpa, times(1)).saveAll(any(Flux.class));
    }
}
