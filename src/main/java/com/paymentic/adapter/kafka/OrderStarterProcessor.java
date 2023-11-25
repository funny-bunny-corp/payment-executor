package com.paymentic.adapter.kafka;

import com.paymentic.domain.payment.events.PaymentOrderStartedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OrderStarterProcessor {
  private static final Logger LOGGER = Logger.getLogger(OrderStarterProcessor.class);
  private final Emitter<PaymentOrderStartedEvent> paymentOrderStartedEventEmitter;

  public OrderStarterProcessor(@Channel("payment-order-started") Emitter<PaymentOrderStartedEvent> paymentOrderStartedEventEmitter) {
    this.paymentOrderStartedEventEmitter = paymentOrderStartedEventEmitter;
  }
  public void notify(@Observes(during = TransactionPhase.AFTER_SUCCESS) PaymentOrderStartedEvent event){
    LOGGER.info("Emitting payment order started...");
    this.paymentOrderStartedEventEmitter.send(event);
    LOGGER.info("Payment Order started emitted!!!");
  }

}
