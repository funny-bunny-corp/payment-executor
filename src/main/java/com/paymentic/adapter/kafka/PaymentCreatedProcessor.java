package com.paymentic.adapter.kafka;

import com.paymentic.domain.payment.PaymentOrderReceived;
import com.paymentic.domain.payment.events.PaymentCreatedEvent;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.ce.IncomingCloudEventMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PaymentCreatedProcessor {

  private static final Logger LOGGER = Logger.getLogger(PaymentCreatedProcessor.class);

  private final Event<PaymentOrderReceived> trigger;

  public PaymentCreatedProcessor(Event<PaymentOrderReceived> trigger) {
    this.trigger = trigger;
  }

  @Incoming("payment-created")
  @Blocking
  public CompletionStage<Void> process(Message<PaymentCreatedEvent> message) {
    var event = message.getMetadata(IncomingCloudEventMetadata.class).orElseThrow(() -> new IllegalArgumentException("Expected a Cloud Event"));
    var paymentCreated = message.getPayload();
    if (Objects.nonNull(paymentCreated.payments())){
      paymentCreated.payments().forEach(paymentOrder -> {
        this.trigger.fire(new PaymentOrderReceived(paymentOrder.id(),paymentOrder.amount(),paymentOrder.currency(),paymentOrder.status(),
            LocalDateTime.now(),paymentCreated.checkout()));
      });
    }
    return message.ack();
  }

}
