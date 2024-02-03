package com.paymentic.adapter.kafka.out;

import com.paymentic.domain.payment.events.PaymentOrderStartedEvent;
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
public class PaymentOrderStartedProcessor {
  private static final Logger LOGGER = Logger.getLogger(PaymentOrderStartedProcessor.class);
  private final Emitter<PaymentOrderStartedEvent> paymentOrderStartedEventEmitter;

  public PaymentOrderStartedProcessor(@Channel("payment-order-started") Emitter<PaymentOrderStartedEvent> paymentOrderStartedEventEmitter) {
    this.paymentOrderStartedEventEmitter = paymentOrderStartedEventEmitter;
  }
  public void notify(@Observes(during = TransactionPhase.AFTER_SUCCESS) PaymentOrderStartedEvent event){
    LOGGER.info(String.format("Starting process to pay payment order id %s", event.id()));
    var metadata = OutgoingCloudEventMetadata.builder()
        .withExtensions(new ExtensionsBuilder().audience(Audience.EXTERNAL_BOUNDED_CONTEXT).eventContext(
            EventContext.DOMAIN).build())
        .build();
    this.paymentOrderStartedEventEmitter.send(Message.of(event).addMetadata(metadata));
    LOGGER.info(String.format("Payment order id started %s",event.id()));
  }

}
