package com.paymentic.adapter.kafka;

import com.paymentic.domain.payment.events.PaymentOrderStartedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class OrderStarterProcessor {
  private final Emitter<PaymentOrderStartedEvent> paymentOrderStartedEventEmitter;
  public OrderStarterProcessor(@Channel("payment-order-started") Emitter<PaymentOrderStartedEvent> paymentOrderStartedEventEmitter) {
    this.paymentOrderStartedEventEmitter = paymentOrderStartedEventEmitter;
  }
  public void notify(@Observes(during = TransactionPhase.BEFORE_COMPLETION) PaymentOrderStartedEvent event){
    this.paymentOrderStartedEventEmitter.send(event);
  }

}
