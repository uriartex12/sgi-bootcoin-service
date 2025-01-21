package com.sgi.bootcoin.application.service.impl;

import com.sgi.bootcoin.Infrastructure.subscriber.events.BankAccountExistEvent;
import com.sgi.bootcoin.Infrastructure.subscriber.events.OrchestratorBootcoinEventResponse;
import com.sgi.bootcoin.Infrastructure.subscriber.events.WalletExistEvent;
import com.sgi.bootcoin.application.service.EventHandleService;
import com.sgi.bootcoin.application.service.RedisService;
import com.sgi.bootcoin.domain.port.BootcoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.sgi.bootcoin.infrastructure.dto.PurchaseResponse.StatusEnum.COMPLETED;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventHandleServiceImpl implements EventHandleService {

    private final BootcoinService bootcoinService;
    private final RedisService redisService;

    @Override
    public void validationExistBankAccount(BankAccountExistEvent bankAccountExistEvent) {
        if (bankAccountExistEvent.getExist()){
            bootcoinService.associateBankAccount(bankAccountExistEvent.getBootcoinId(),
                    bankAccountExistEvent.getAccountId()).subscribe();
        }else {
            log.error("Invalid bank account : {} ", bankAccountExistEvent.getAccountId());
        }
    }

    @Override
    public void validationExistYanki(WalletExistEvent walletExistEvent) {
        if (walletExistEvent.getExist()){
            bootcoinService.associateYanki(walletExistEvent.getBootcoinId(),
                    walletExistEvent.getYankiId()).subscribe();
        }else {
            log.error("Invalid yanki Id : {} ", walletExistEvent.getYankiId());
        }
    }

    @Override
    public void updateStatePurchaseOrder(OrchestratorBootcoinEventResponse bootcoinEvent) {
        Mono.just( redisService.findAllBootCoinOrderByOperation(bootcoinEvent.getOperation()))
                .flatMap(bootCoinOrder -> {
                    bootCoinOrder.setStatus(COMPLETED.name());
                    return redisService.saveBootCoinOrder(bootcoinEvent.getOperation(),
                            Mono.just(bootCoinOrder));
                }).subscribe();
    }

    @Override
    public void invalidatePurchaseProcess(OrchestratorBootcoinEventResponse bootcoinEvent) {
        bootcoinService.invalidatePurchaseProcess(bootcoinEvent).subscribe();
    }


}
