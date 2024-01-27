package com.paymentic.adapter.kafka.out;

import com.paymentic.domain.payment.events.RefundOrderStarted;
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
public class OrderStarterProcessor {
  private static final Logger LOGGER = Logger.getLogger(OrderStarterProcessor.class);
  private final Emitter<RefundOrderStarted> paymentOrderStartedEventEmitter;

  public OrderStarterProcessor(@Channel("payment-order-started") Emitter<RefundOrderStarted> paymentOrderStartedEventEmitter) {
    this.paymentOrderStartedEventEmitter = paymentOrderStartedEventEmitter;
  }
  public void notify(@Observes(during = TransactionPhase.AFTER_SUCCESS) RefundOrderStarted event){
    LOGGER.info(String.format("Starting process to pay payment order id %s", event.id()));
    var metadata = OutgoingCloudEventMetadata.builder()
        .withExtensions(new ExtensionsBuilder().audience(Audience.EXTERNAL_BOUNDED_CONTEXT).eventContext(
            EventContext.DOMAIN).build())
        .build();
    this.paymentOrderStartedEventEmitter.send(Message.of(event).addMetadata(metadata));
    LOGGER.info(String.format("Payment order id started %s",event.id()));
  }

}
