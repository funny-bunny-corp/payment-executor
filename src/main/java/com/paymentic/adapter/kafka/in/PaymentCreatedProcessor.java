package com.paymentic.adapter.kafka.in;

import com.paymentic.domain.payment.PaymentOrderReceived;
import com.paymentic.domain.payment.events.PaymentCreatedEvent;
import com.paymentic.infra.events.repository.EventRepository;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.ce.IncomingCloudEventMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PaymentCreatedProcessor {
  private static final String PAYMENT_ORDER_CREATED_EVENT_TYPE = "paymentic.io.payment-processing.v1.payment.created";
  private static final Logger LOGGER = Logger.getLogger(PaymentCreatedProcessor.class);
  private final Event<PaymentOrderReceived> trigger;
  private final EventRepository eventRepository;
  public PaymentCreatedProcessor(Event<PaymentOrderReceived> trigger,
      EventRepository eventRepository) {
    this.trigger = trigger;
    this.eventRepository = eventRepository;
  }
  @Blocking
  @Incoming("payment-created")
  public CompletionStage<Void> process(Message<PaymentCreatedEvent> message) {
    var event = message.getMetadata(IncomingCloudEventMetadata.class).orElseThrow(() -> new IllegalArgumentException("Expected a Cloud Event"));
    var paymentCreated = message.getPayload();
    var handle = eventRepository.shouldHandle(new com.paymentic.infra.events.Event(UUID.fromString(event.getId())));
    if (handle){
      if (PAYMENT_ORDER_CREATED_EVENT_TYPE.equals(event.getType())){
        LOGGER.info(String.format("Receiving payment created event. Checkout-Id %s Event-Id %s. Start processing....",paymentCreated.checkout().getId().toString(),event.getId()));
        if (Objects.nonNull(paymentCreated.payments())){
          paymentCreated.payments().forEach(paymentOrder -> {
            LOGGER.info("Triggering internal process");
            LOGGER.info(String.format("Triggering payment order created. Payment-Order-Id %s .  Start processing....",paymentCreated.checkout().getId().toString()));
            this.trigger.fire(new PaymentOrderReceived(UUID.fromString(paymentOrder.id()),paymentOrder.amount(),paymentOrder.currency(),paymentOrder.status(),
                LocalDateTime.now(),paymentCreated.checkout(),paymentOrder.sellerInfo()));
            LOGGER.info(String.format("Payment order triggered. Payment-Order-Id %s . ",paymentCreated.checkout().getId().toString()));
          });
        }
        LOGGER.info(String.format("Payment created event processed. Checkout-Id %s Event-Id %s. Start processing....",paymentCreated.checkout().getId().toString(),event.getId()));
      }
    }
    return message.ack();
  }

}
