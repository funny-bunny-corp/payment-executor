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
  private final Emitter<TransactionProcessedEvent> paymentOrderFailedEmitter;
  private final Emitter<TransactionProcessedEvent> paymentOrderApprovedEmitter;
  private final Emitter<TransactionProcessedEvent> refundFailedEmitter;
  private final Emitter<TransactionProcessedEvent> refundApprovedEmitter;
  public TransactionResultProcessor(
      @Channel("payment-order-failed") Emitter<TransactionProcessedEvent> paymentOrderFailedEmitter,
      @Channel("payment-order-approved") Emitter<TransactionProcessedEvent> paymentOrderApprovedEmitter,
      @Channel("refund-failed") Emitter<TransactionProcessedEvent> refundFailedEmitter,
      @Channel("refund-approved") Emitter<TransactionProcessedEvent> refundApprovedEmitter) {
    this.paymentOrderFailedEmitter = paymentOrderFailedEmitter;
    this.paymentOrderApprovedEmitter = paymentOrderApprovedEmitter;
    this.refundApprovedEmitter = refundApprovedEmitter;
    this.refundFailedEmitter = refundFailedEmitter;
  }
  public void notify(@Observes(during = TransactionPhase.AFTER_SUCCESS  ) TransactionProcessedEvent transactionProcessedEvent){
    LOGGER.info(String.format("Transaction type %s processing %s",transactionProcessedEvent.type(),transactionProcessedEvent.id()));
    var metadata = OutgoingCloudEventMetadata.builder()
        .withExtensions(new ExtensionsBuilder().audience(Audience.EXTERNAL_BOUNDED_CONTEXT).eventContext(
            EventContext.DOMAIN).build())
        .build();
    if (transactionProcessedEvent.isCheckout()){
      if (TransactionStatus.APPROVED.equals(transactionProcessedEvent.getStatus())){
        this.paymentOrderApprovedEmitter.send(Message.of(transactionProcessedEvent).addMetadata(metadata));
      }else {
        this.paymentOrderFailedEmitter.send(Message.of(transactionProcessedEvent).addMetadata(metadata));
      }
    } else if (transactionProcessedEvent.isRefund()){
      if (TransactionStatus.APPROVED.equals(transactionProcessedEvent.getStatus())){
        this.refundApprovedEmitter.send(Message.of(transactionProcessedEvent).addMetadata(metadata));
      }else {
        this.refundFailedEmitter.send(Message.of(transactionProcessedEvent).addMetadata(metadata));
      }
    } else{
      throw new IllegalArgumentException("Transaction type not supported");
    }
    LOGGER.info(String.format("Transaction type %s id processed %s",transactionProcessedEvent.type(),transactionProcessedEvent.id()));
  }

}
