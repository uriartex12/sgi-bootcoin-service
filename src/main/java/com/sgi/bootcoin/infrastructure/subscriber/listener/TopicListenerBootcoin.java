package com.sgi.bootcoin.infrastructure.subscriber.listener;

import com.sgi.bootcoin.infrastructure.annotations.KafkaController;
import com.sgi.bootcoin.infrastructure.subscriber.events.BankAccountExistEvent;
import com.sgi.bootcoin.infrastructure.subscriber.events.OrchestratorBootcoinEventResponse;
import com.sgi.bootcoin.infrastructure.subscriber.events.WalletExistEvent;
import com.sgi.bootcoin.application.service.EventHandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

import static com.sgi.bootcoin.infrastructure.enums.PaymentProcess.PAYMENT_CANCELLED;
import static com.sgi.bootcoin.infrastructure.enums.PaymentProcess.PAYMENT_COMPLETED;

@KafkaController
@RequiredArgsConstructor
@Slf4j
public class TopicListenerBootcoin {


    private final EventHandleService eventHandleService;

    @KafkaListener(topics = BankAccountExistEvent.TOPIC, groupId = "${app.name}")
    public void listenForValidationResponse(BankAccountExistEvent event) {
        eventHandleService.validationExistBankAccount(event);
    }

    @KafkaListener(topics = WalletExistEvent.TOPIC, groupId = "${app.name}")
    public void listenForValidationResponse(WalletExistEvent event) {
        eventHandleService.validationExistYanki(event);
    }

    @KafkaListener(topics = OrchestratorBootcoinEventResponse.TOPIC, groupId = "${app.name}")
    public void listenForValidationResponse(OrchestratorBootcoinEventResponse event) {
        log.info(" state {}, result {}", event.getStatus(), event);
        if (event.getStatus().equals(PAYMENT_CANCELLED.name())){
            eventHandleService.invalidatePurchaseProcess(event);
        }else if (event.getStatus().equals(PAYMENT_COMPLETED.name())){
            eventHandleService.updateStatePurchaseOrder(event);
        }
    }
}