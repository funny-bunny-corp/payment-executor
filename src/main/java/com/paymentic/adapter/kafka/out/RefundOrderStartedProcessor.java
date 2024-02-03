package com.paymentic.adapter.kafka.out;

import com.paymentic.domain.payment.events.PaymentOrderStartedEvent;
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
public class RefundOrderStartedProcessor {
  private static final Logger LOGGER = Logger.getLogger(RefundOrderStartedProcessor.class);
  private final Emitter<RefundOrderStarted> refundOrderStartedEmitter;
  public RefundOrderStartedProcessor(@Channel("refund-started") Emitter<RefundOrderStarted> refundOrderStartedEmitter) {
    this.refundOrderStartedEmitter = refundOrderStartedEmitter;
  }
  public void notify(@Observes(during = TransactionPhase.AFTER_SUCCESS) RefundOrderStarted event){
    LOGGER.info(String.format("Starting process to refund order id %s", event.id()));
    var metadata = OutgoingCloudEventMetadata.builder()
        .withExtensions(new ExtensionsBuilder().audience(Audience.EXTERNAL_BOUNDED_CONTEXT).eventContext(
            EventContext.DOMAIN).build())
        .build();
    this.refundOrderStartedEmitter.send(Message.of(event).addMetadata(metadata));
    LOGGER.info(String.format("Refund id started %s",event.id()));
  }

}
