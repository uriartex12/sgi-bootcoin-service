package com.sgi.bootcoin.infrastructure.controller;

import com.sgi.bootcoin.application.service.impl.BootcoinServiceImpl;
import com.sgi.bootcoin.application.service.impl.ExchangeRateServiceImpl;
import com.sgi.bootcoin.helper.FactoryTest;
import com.sgi.bootcoin.infrastructure.dto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;

/**
 * Test class for the {@link BootcoinControllerTest}.
 * Utilizes {@code @WebFluxTest} to test the controller layer in isolation
 * without starting the full application context.
 */
@WebFluxTest(controllers = BootcoinController.class)
public class BootcoinControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ExchangeRateServiceImpl exchangeRateService;

    @MockBean
    private BootcoinServiceImpl bootcoinService;

    @Test
    void createBootCoin_shouldReturnCreatedResponse() {
        BootcoinResponse bootcoinResponse = FactoryTest.toFactoryBootcoin(BootcoinResponse.class);
        Mockito.when(bootcoinService.createBootcoin(any(Mono.class)))
                .thenReturn(Mono.just(bootcoinResponse));

        webTestClient.post()
                .uri("/v1/bootcoin")
                .bodyValue(FactoryTest.toFactoryBootcoin(BootcoinRequest.class))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BootcoinResponse.class)
                .consumeWith(accountResponseEntityExchangeResult -> {
                    BootcoinResponse actual = accountResponseEntityExchangeResult.getResponseBody();
                    Assertions.assertNotNull(Objects.requireNonNull(actual).getId());
                    Assertions.assertNotNull(actual.getName());
                })
                .returnResult();
        Mockito.verify(bootcoinService, times(1)).createBootcoin(any(Mono.class));
    }


    @Test
    void createBExchangeRate_shouldReturnCreatedResponse() {
        RateResponse rateResponse = FactoryTest.toFactoryExchangeRate(RateResponse.class);
        Mockito.when(exchangeRateService.createExchangeRate(any(Mono.class)))
                .thenReturn(Mono.just(rateResponse));

        webTestClient.post()
                .uri("/v1/bootcoin/exchange-rate")
                .bodyValue(FactoryTest.toFactoryExchangeRate(RateRequest.class))
                .exchange()
                .expectStatus().isOk()
                .expectBody(RateResponse.class)
                .consumeWith(accountResponseEntityExchangeResult -> {
                    RateResponse actual = accountResponseEntityExchangeResult.getResponseBody();
                    Assertions.assertNotNull(Objects.requireNonNull(actual).getSellRate());
                    Assertions.assertNotNull(actual.getBuyRate());
                })
                .returnResult();
        Mockito.verify(exchangeRateService, times(1)).createExchangeRate(any(Mono.class));
    }

    @Test
    void deleteBootCoin_shouldReturnOkResponse() {
        String bootCoinId = randomUUID().toString();
        Mockito.when(bootcoinService.deleteBootcoin(bootCoinId)).thenReturn(Mono.empty());
        webTestClient.delete()
                .uri("/v1/bootcoin/{bootcoinId}", bootCoinId)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void getBootcoinById_shouldReturnBootcoinResponse() {
        String bootCoinId = randomUUID().toString();
        BootcoinResponse bootcoinResponse = FactoryTest.toFactoryBootcoin(BootcoinResponse.class);
        bootcoinResponse.setId(bootCoinId);
        Mockito.when(bootcoinService.getBootcoinById(bootCoinId))
                .thenReturn(Mono.just(bootcoinResponse));
        webTestClient.get()
                .uri("/v1/bootcoin/{bootcoinId}", bootCoinId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BootcoinResponse.class)
                .consumeWith(System.out::println)
                .value(actual -> {
                    Assertions.assertEquals(bootcoinResponse.getId(), actual.getId());
                });
    }

    @Test
    void getAllBootcoin_shouldReturnFluxOfBootcoinResponse() {
        List<BootcoinResponse> bootcoinList = FactoryTest.toFactoryListBootcoinResponse();
        Flux<BootcoinResponse> bootcoinFlux = Flux.fromIterable(bootcoinList);
        Mockito.when(bootcoinService.getAllBootcoin()).thenReturn(bootcoinFlux);
        webTestClient.get()
                .uri("/v1/bootcoin")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BootcoinResponse.class)
                .value(list -> assertThat(list).hasSize(1));
    }

    @Test
    void getAllExchangeRate_shouldReturnFluxOfRateResponse() {
        List<RateResponse> rateResponseList = FactoryTest.toFactoryListRateResponse();
        Flux<RateResponse> rateResponseFlux = Flux.fromIterable(rateResponseList);
        Mockito.when(exchangeRateService.getAllExchangeRate()).thenReturn(rateResponseFlux);
        webTestClient.get()
                .uri("/v1/bootcoin/exchange-rate")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BootcoinResponse.class)
                .value(list -> assertThat(list).hasSize(1));
    }

    @Test
    void updateBootCoin_shouldReturnBootcoinResponse() {
        String bootcoinId = randomUUID().toString();
        BootcoinRequest bootcoinRequest = FactoryTest.toFactoryBootcoin(BootcoinRequest.class);
        BootcoinResponse bootcoinResponse = FactoryTest.toFactoryBootcoin(BootcoinResponse.class);
        bootcoinResponse.id(bootcoinId);
        Mockito.when(bootcoinService.updateBootcoin(eq(bootcoinId), any(Mono.class)))
                .thenReturn(Mono.just(bootcoinResponse));
        webTestClient.put()
                .uri("/v1/bootcoin/{bootcoinId}", bootcoinId)
                .bodyValue(bootcoinRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BootcoinResponse.class);
        Mockito.verify(bootcoinService, times(1)).updateBootcoin(eq(bootcoinId), any(Mono.class));
    }


    @Test
    void getBootcoinBalance_shouldReturnBalanceResponse() {
        String phone = "910677465";
        BalanceResponse balanceResponse = FactoryTest.toFactoryBalance(phone);
        Mockito.when(bootcoinService.getBootcoinBalance(phone))
                .thenReturn(Mono.just(balanceResponse));
        webTestClient.get()
                .uri("/v1/bootcoin/{phone}/balance", phone)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BalanceResponse.class)
                .consumeWith(System.out::println)
                .value(actual -> {
                    Assertions.assertEquals(balanceResponse.getPhone(), actual.getPhone());
                });
    }


    @Test
    void getPurchaseRequest_shouldReturnPurchaseResponse() {
        PurchaseRequest purchaseRequest = FactoryTest.toFactoryPurchase(PurchaseRequest.class);
        PurchaseResponse purchaseResponse = FactoryTest.toFactoryPurchase(PurchaseResponse.class);
        purchaseResponse.setBuyerId(purchaseRequest.getBuyerId());

        Mockito.when(bootcoinService.createPurchaseBootcoin(any(Mono.class)))
                .thenReturn(Mono.just(purchaseResponse));

        webTestClient.post()
                .uri("/v1/bootcoin/purchase")
                .bodyValue(FactoryTest.toFactoryExchangeRate(RateRequest.class))
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(PurchaseResponse.class)
                .consumeWith(System.out::println)
                .value(actual -> {
                    Assertions.assertEquals(purchaseResponse.getBuyerId(), actual.getBuyerId());
                });
    }


    @Test
    void associateWalletOrAccount_shouldReturnAssociateResponse() {
        String action = "yanki";
        String bootcoinId = UUID.randomUUID().toString();
        AssociateRequest associateRequest = FactoryTest.toFactoryAssociateRequest();
        AssociateResponse associateResponse = FactoryTest.toFactoryAssociateResponse();
        associateResponse.setBootcoinId(bootcoinId);

        Mockito.when(bootcoinService.associateWalletOrAccount(anyString(), anyString(), any(Mono.class)))
                .thenReturn(Mono.just(associateResponse));

        webTestClient.post()
                .uri("/v1/bootcoin/{bootcoinId}/associate/{action}",bootcoinId, action)
                .bodyValue(associateRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(AssociateResponse.class)
                .value(actual ->  Assertions.assertEquals(associateResponse.getBootcoinId(), actual.getBootcoinId()));
    }

    @Test
    void acceptPurchase_shouldReturnAcceptPurchaseRequest() {
        String operation = UUID.randomUUID().toString();
        AssociateRequest associateRequest = FactoryTest.toFactoryAssociateRequest();
        PurchaseResponse purchaseResponse = FactoryTest.toFactoryBootcoin(PurchaseResponse.class);
        purchaseResponse.setStatus(PurchaseResponse.StatusEnum.ACCEPTED);

        Mockito.when(bootcoinService.acceptPurchaseBootcoin(anyString(), any(Mono.class)))
                .thenReturn(Mono.just(purchaseResponse));

        webTestClient.post()
                .uri("/v1/bootcoin/accept-purchase/{operation}", operation)
                .bodyValue(associateRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(PurchaseResponse.class)
                .value(actual ->  Assertions.assertEquals(purchaseResponse.getOperation(), actual.getOperation()));
    }

}
