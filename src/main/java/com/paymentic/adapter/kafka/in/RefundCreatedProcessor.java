package com.paymentic.adapter.kafka.in;

import com.paymentic.domain.payment.Refund;
import com.paymentic.domain.payment.RefundReceived;
import com.paymentic.domain.payment.events.RefundCreatedEvent;
import com.paymentic.domain.shared.PaymentOrderId;
import com.paymentic.infra.events.repository.EventRepository;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.ce.IncomingCloudEventMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import org.apache.kafka.common.protocol.types.Field.Str;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

@ApplicationScoped
public class RefundCreatedProcessor {
  private static final String REFUND_CREATED_EVENT_TYPE = "paymentic.io.payment-processing.v1.refund.created";
  private static final Logger LOGGER = Logger.getLogger(RefundCreatedProcessor.class);
  private final Event<RefundReceived> trigger;
  private final EventRepository eventRepository;
  public RefundCreatedProcessor(Event<RefundReceived> trigger,
      EventRepository eventRepository) {
    this.trigger = trigger;
    this.eventRepository = eventRepository;
  }
  @Blocking
  @Incoming("refund-created")
  public CompletionStage<Void> process(Message<RefundCreatedEvent> message) {
    var event = message.getMetadata(IncomingCloudEventMetadata.class).orElseThrow(() -> new IllegalArgumentException("Expected a Cloud Event"));
    var refundCreatedEvent = message.getPayload();
    var handle = eventRepository.shouldHandle(new com.paymentic.infra.events.Event(UUID.fromString(event.getId())));
    if (handle){
      if (REFUND_CREATED_EVENT_TYPE.equals(event.getType())){
        LOGGER.info(String.format("Receiving refund created event. Refund-Id %s Event-Id %s. Start processing....",refundCreatedEvent.refund().id(),event.getId()));
        var refund = new Refund(refundCreatedEvent.refund().id(), refundCreatedEvent.refund().amount(),refundCreatedEvent.refund().currency(),refundCreatedEvent.refund().cardInfo(),refundCreatedEvent.refund().buyerInfo());
        this.trigger.fire(new RefundReceived(refund,refundCreatedEvent.payment()));
        LOGGER.info(String.format("Refund created event processed. Refund-Id %s Event-Id %s. Start processing....",refundCreatedEvent.refund().id(),event.getId()));
      }
    }
    return message.ack();
  }

}
