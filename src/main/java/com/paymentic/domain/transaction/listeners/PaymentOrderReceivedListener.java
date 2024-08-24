package com.paymentic.domain.transaction.listeners;

import com.paymentic.adapter.http.PspRestClient;
import com.paymentic.domain.payment.PaymentOrderReceived;
import com.paymentic.domain.payment.events.PaymentOrderStartedEvent;
import com.paymentic.domain.psp.PaymentRequest;
import com.paymentic.domain.shared.CheckoutId;
import com.paymentic.domain.shared.PaymentOrderId;
import com.paymentic.domain.transaction.Transaction;
import com.paymentic.domain.transaction.TransactionId;
import com.paymentic.domain.transaction.TransactionType;
import com.paymentic.domain.transaction.events.TransactionProcessedEvent;
import com.paymentic.domain.transaction.repositories.TransactionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PaymentOrderReceivedListener {
  private static final Logger LOGGER = Logger.getLogger(PaymentOrderReceivedListener.class);
  private final TransactionRepository transactionRepository;
  private final PspRestClient pspRestClient;
  private final Event<TransactionProcessedEvent> transactionTrigger;
  private final Event<PaymentOrderStartedEvent> orderStartedTrigger;

  public PaymentOrderReceivedListener(TransactionRepository transactionRepository,
      @RestClient PspRestClient pspRestClient,
      Event<TransactionProcessedEvent> trigger,
      Event<PaymentOrderStartedEvent> orderStartedTrigger) {
    this.transactionRepository = transactionRepository;
    this.pspRestClient = pspRestClient;
    this.transactionTrigger = trigger;
    this.orderStartedTrigger = orderStartedTrigger;
  }
  @Transactional
  void paymentOrderReceived(@Observes PaymentOrderReceived paymentOrder){
    LOGGER.info("Payment Order received starting process....");
    var transactionReceived = Transaction.newTransactionReceived(new PaymentOrderId(paymentOrder.id()),paymentOrder.amount(),paymentOrder.currency(),paymentOrder.buyer(),paymentOrder.checkout().getPaymentType(), TransactionType.PAYMENT);
    this.transactionRepository.persist(transactionReceived);
    LOGGER.info("Triggering order started..");
    this.orderStartedTrigger.fire(new PaymentOrderStartedEvent(paymentOrder.id().toString(),paymentOrder.amount(),paymentOrder.currency(),paymentOrder.seller(),paymentOrder.at().toLocalDate().toString()));
    LOGGER.info("Order started fired!!!");
    LOGGER.info("Calling PSP integration...");
    var paymentResult = this.pspRestClient.pay(new PaymentRequest(paymentOrder.amount()));
    LOGGER.info("PSP executed successfully!!!");
    var transactionProcessed = Transaction.newTransactionProcessed(new PaymentOrderId(paymentOrder.id()),paymentOrder.amount(),paymentOrder.currency(),paymentOrder.buyer(),paymentOrder.checkout().getPaymentType(),paymentResult.getStatus(),TransactionType.PAYMENT);
    this.transactionRepository.persist(transactionProcessed);
    var event = TransactionProcessedEvent.ofCheckout(new TransactionId(transactionProcessed.getId()),paymentOrder.seller(),new PaymentOrderId(paymentOrder.id()),new CheckoutId(paymentOrder.checkout().getId()),paymentOrder.amount(),paymentOrder.currency(),
        LocalDateTime.now(),paymentOrder.buyer(),transactionProcessed.getStatus());
    this.transactionTrigger.fire(event);
    LOGGER.info("Payment Order processed successfully!!!");
  }

}
