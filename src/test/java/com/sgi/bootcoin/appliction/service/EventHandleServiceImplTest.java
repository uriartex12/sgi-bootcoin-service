package com.sgi.bootcoin.appliction.service;

import com.sgi.bootcoin.infrastructure.subscriber.events.BankAccountExistEvent;
import com.sgi.bootcoin.infrastructure.subscriber.events.OrchestratorBootcoinEventResponse;
import com.sgi.bootcoin.infrastructure.subscriber.events.WalletExistEvent;
import com.sgi.bootcoin.application.service.RedisService;
import com.sgi.bootcoin.application.service.impl.EventHandleServiceImpl;
import com.sgi.bootcoin.domain.model.redis.BootCoinOrder;
import com.sgi.bootcoin.domain.port.BootcoinService;
import com.sgi.bootcoin.helper.FactoryTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventHandleServiceImplTest {

    @Mock
    private BootcoinService bootcoinService;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private EventHandleServiceImpl eventHandleService;

    @Test
    void validationExistBankAccount_shouldAssociateBankAccountWhenExists() {
        BankAccountExistEvent bankAccountExistEvent = FactoryTest.toFactoryBankAccountExist();

        when(bootcoinService.associateBankAccount(anyString(), anyString()))
                .thenReturn(Mono.empty());

        eventHandleService.validationExistBankAccount(bankAccountExistEvent);

        verify(bootcoinService, times(1)).associateBankAccount(anyString(), anyString());
    }

    @Test
    void validationExistBankAccount_shouldLogErrorWhenNotExists() {
        BankAccountExistEvent bankAccountExistEvent = FactoryTest.toFactoryBankAccountExist();
        bankAccountExistEvent.setExist(false);

        eventHandleService.validationExistBankAccount(bankAccountExistEvent);

        verify(bootcoinService, never()).associateBankAccount(any(), any());
    }

    @Test
    void validationExistYanki_shouldAssociateYankiWhenExists() {
        WalletExistEvent walletExistEvent = FactoryTest.toFactoryWalletExistEvent();

        when(bootcoinService.associateYanki(walletExistEvent.getBootcoinId(),
                walletExistEvent.getYankiId(), walletExistEvent.getWalletDetail()))
                .thenReturn(Mono.empty());

        eventHandleService.validationExistYanki(walletExistEvent);

        verify(bootcoinService, times(1)).associateYanki(walletExistEvent.getBootcoinId(),
                walletExistEvent.getYankiId(), walletExistEvent.getWalletDetail());
    }

    @Test
    void validationExistYanki_shouldLogErrorWhenNotExists() {
        WalletExistEvent walletExistEvent = FactoryTest.toFactoryWalletExistEvent();
        walletExistEvent.setExist(false);
        eventHandleService.validationExistYanki(walletExistEvent);

        verify(bootcoinService, never()).associateYanki(any(), any(), any());
    }

    @Test
    void updateStatePurchaseOrder_shouldUpdateBootcoinOrder() {
        OrchestratorBootcoinEventResponse bootcoinEvent = FactoryTest.toFactoryOrchestratorBootcoin();
        BootCoinOrder bootCoinOrder = FactoryTest.toFactoryBootcoinOrder();

        when(redisService.findAllBootCoinOrderByOperation(bootcoinEvent.getOperation()))
                .thenReturn(bootCoinOrder);

        when(redisService.saveBootCoinOrder(eq(bootcoinEvent.getOperation()), any(Mono.class)))
                .thenReturn(Mono.just(bootCoinOrder));

        eventHandleService.updateStatePurchaseOrder(bootcoinEvent);

        verify(redisService, times(1)).findAllBootCoinOrderByOperation(bootcoinEvent.getOperation());
        verify(redisService, times(1)).saveBootCoinOrder(eq(bootcoinEvent.getOperation()), any(Mono.class));
    }

    @Test
    void invalidatePurchaseProcess_shouldCallInvalidatePurchaseProcess() {
        OrchestratorBootcoinEventResponse bootcoinEvent = FactoryTest.toFactoryOrchestratorBootcoin();

        when(bootcoinService.invalidatePurchaseProcess(bootcoinEvent)).thenReturn(Mono.empty());

        eventHandleService.invalidatePurchaseProcess(bootcoinEvent);

        verify(bootcoinService, times(1)).invalidatePurchaseProcess(bootcoinEvent);
    }
}
