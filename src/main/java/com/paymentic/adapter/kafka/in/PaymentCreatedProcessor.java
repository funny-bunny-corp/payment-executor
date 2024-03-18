package com.paymentic.adapter.kafka.in;

import com.paymentic.domain.payment.PaymentOrderReceived;
import com.paymentic.domain.payment.events.PaymentCreatedEvent;
import com.paymentic.infra.events.repository.EventRepository;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.ce.IncomingCloudEventMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PaymentCreatedProcessor {
  private static final String PAYMENT_ORDER_CREATED_EVENT_TYPE = "funny-bunny.xyz.payment-processing.v1.payment.created";
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
        LOGGER.info(String.format("Receiving payment created event. Checkout-Id %s Event-Id %s. Start processing....",paymentCreated.transaction().order().getId().toString(),event.getId()));
        var payment = new PaymentOrderReceived(UUID.fromString(paymentCreated.transaction().payment().id()),paymentCreated.transaction().payment().amount(),paymentCreated.transaction().payment().currency(),
            paymentCreated.transaction().payment().status(), LocalDateTime.now(),paymentCreated.transaction().order(),paymentCreated.transaction().participants().seller(),paymentCreated.transaction().participants().buyer());
        this.trigger.fire(payment);
        LOGGER.info(String.format("Payment created event processed. Checkout-Id %s Event-Id %s. Start processing....",paymentCreated.transaction().order().getId().toString(),event.getId()));
      }
    }
    return message.ack();
  }

}
