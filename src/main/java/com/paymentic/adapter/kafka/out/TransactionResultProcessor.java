package com.paymentic.adapter.kafka.out;

import com.paymentic.domain.transaction.TransactionStatus;
import com.paymentic.domain.transaction.events.TransactionProcessedEvent;
import com.paymentic.infra.ce.CExtensions.Audience;
import com.paymentic.infra.ce.CExtensions.EventContext;
import com.paymentic.infra.ce.ExtensionsBuilder;
import io.smallrye.reactive.messaging.ce.OutgoingCloudEventMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TransactionResultProcessor {
  private static final Logger LOGGER = Logger.getLogger(TransactionResultProcessor.class);
  private final Emitter<TransactionProcessedEvent> transactionFailedEmitter;
  private final Emitter<TransactionProcessedEvent> transactionApprovedEmitter;
  public TransactionResultProcessor( @Channel("transaction-failed") Emitter<TransactionProcessedEvent> transactionProcessedEmitter,
      @Channel("transaction-approved") Emitter<TransactionProcessedEvent> transactionApprovedEmitter) {
    this.transactionFailedEmitter = transactionProcessedEmitter;
    this.transactionApprovedEmitter = transactionApprovedEmitter;
  }
  public void notify(@Observes(during = TransactionPhase.AFTER_SUCCESS  ) TransactionProcessedEvent transactionProcessedEvent){
    LOGGER.info(String.format("Transaction type %s processing %s",transactionProcessedEvent.type(),transactionProcessedEvent.id()));
    var metadata = OutgoingCloudEventMetadata.builder()
        .withExtensions(new ExtensionsBuilder().audience(Audience.EXTERNAL_BOUNDED_CONTEXT).eventContext(
            EventContext.DOMAIN).build())
        .build();
    if (TransactionStatus.APPROVED.equals(transactionProcessedEvent.getStatus())){
      this.transactionApprovedEmitter.send(Message.of(transactionProcessedEvent).addMetadata(metadata));
    }else {
      this.transactionFailedEmitter.send(Message.of(transactionProcessedEvent).addMetadata(metadata));
    }
    LOGGER.info(String.format("Transaction type %s id processed %s",transactionProcessedEvent.type(),transactionProcessedEvent.id()));
  }

}
