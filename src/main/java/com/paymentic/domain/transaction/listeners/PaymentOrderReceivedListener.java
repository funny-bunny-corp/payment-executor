package com.paymentic.domain.transaction.listeners;

import com.paymentic.adapter.http.PspRestClient;
import com.paymentic.domain.payment.PaymentOrderReceived;
import com.paymentic.domain.payment.events.PaymentOrderStartedEvent;
import com.paymentic.domain.psp.PaymentRequest;
import com.paymentic.domain.shared.CheckoutId;
import com.paymentic.domain.shared.PaymentOrderId;
import com.paymentic.domain.transaction.Transaction;
import com.paymentic.domain.transaction.TransactionId;
import com.paymentic.domain.transaction.events.TransactionProcessedEvent;
import com.paymentic.domain.transaction.repositories.TransactionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class PaymentOrderReceivedListener {
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
    var transactionReceived = Transaction.newTransactionReceived(new PaymentOrderId(paymentOrder.id()),paymentOrder.amount(),paymentOrder.currency(),paymentOrder.checkout()
        .getBuyerInfo(),paymentOrder.checkout().getCardInfo());
    this.transactionRepository.persist(transactionReceived);
    this.orderStartedTrigger.fire(new PaymentOrderStartedEvent(paymentOrder.id().toString()));
    var paymentResult = this.pspRestClient.pay(new PaymentRequest(paymentOrder.amount()));
    var transactionProcessed = Transaction.newTransactionProcessed(new PaymentOrderId(paymentOrder.id()),paymentOrder.amount(),paymentOrder.currency(),paymentOrder.checkout()
        .getBuyerInfo(),paymentOrder.checkout().getCardInfo(),paymentResult.getStatus());
    this.transactionRepository.persist(transactionProcessed);
    var event = new TransactionProcessedEvent(new TransactionId(transactionProcessed.getId()),paymentOrder.seller(),new PaymentOrderId(paymentOrder.id()),new CheckoutId(paymentOrder.checkout().getId()),paymentOrder.amount(),paymentOrder.currency(),
        LocalDateTime.now(),paymentOrder.checkout().getBuyerInfo(),transactionProcessed.getStatus());
    this.transactionTrigger.fire(event);
  }

}
