package com.sgi.bootcoin.application.service;

import com.sgi.bootcoin.infrastructure.subscriber.events.BankAccountExistEvent;
import com.sgi.bootcoin.infrastructure.subscriber.events.OrchestratorBootcoinEventResponse;
import com.sgi.bootcoin.infrastructure.subscriber.events.WalletExistEvent;

public interface EventHandleService {
    void validationExistBankAccount(BankAccountExistEvent bankAccountExistEvent);
    void validationExistYanki(WalletExistEvent walletExistEvent);
    void updateStatePurchaseOrder(OrchestratorBootcoinEventResponse bootcoinEvent);
    void invalidatePurchaseProcess(OrchestratorBootcoinEventResponse bootcoinEvent);
}
