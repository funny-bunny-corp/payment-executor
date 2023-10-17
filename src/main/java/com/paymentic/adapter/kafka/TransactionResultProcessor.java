package com.paymentic.adapter.kafka;

import com.paymentic.domain.transaction.TransactionStatus;
import com.paymentic.domain.transaction.events.TransactionProcessedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class TransactionResultProcessor {
  private final Emitter<TransactionProcessedEvent> transactionFailedEmitter;
  private final Emitter<TransactionProcessedEvent> transactionApprovedEmitter;

  public TransactionResultProcessor( @Channel("transaction-failed") Emitter<TransactionProcessedEvent> transactionProcessedEmitter,
      @Channel("transaction-approved") Emitter<TransactionProcessedEvent> transactionApprovedEmitter) {
    this.transactionFailedEmitter = transactionProcessedEmitter;
    this.transactionApprovedEmitter = transactionApprovedEmitter;
  }
  public void notify(@Observes(during = TransactionPhase.AFTER_COMPLETION) TransactionProcessedEvent transactionProcessedEvent){
    if (TransactionStatus.APPROVED.equals(transactionProcessedEvent.status())){
      this.transactionApprovedEmitter.send(transactionProcessedEvent);
    }else {
      this.transactionFailedEmitter.send(transactionProcessedEvent);
    }
  }

}
