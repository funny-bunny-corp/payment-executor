package com.paymentic.domain.transaction.listeners;

import com.paymentic.adapter.http.PspRestClient;
import com.paymentic.domain.payment.PaymentOrderReceived;
import com.paymentic.domain.payment.events.PaymentOrderStartedEvent;
import com.paymentic.domain.psp.PaymentRequest;
import com.paymentic.domain.shared.CheckoutId;
import com.paymentic.domain.shared.PaymentOrderId;
import com.paymentic.domain.transaction.Transaction;
import com.paymentic.domain.transaction.TransactionId;
import com.paymentic.domain.transaction.TransactionStatus;
import com.paymentic.domain.transaction.events.TransactionProcessedEvent;
import com.paymentic.domain.transaction.repositories.TransactionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class PaymentOrderReceivedListener {
  private final TransactionRepository transactionRepository;
  private final PspRestClient pspRestClient;
  private final Emitter<PaymentOrderStartedEvent> paymentOrderStartedEventEmitter;
  private final Emitter<TransactionProcessedEvent> transactionFailedEmitter;
  private final Emitter<TransactionProcessedEvent> transactionApprovedEmitter;

  public PaymentOrderReceivedListener(TransactionRepository transactionRepository,
      @RestClient PspRestClient pspRestClient,
      @Channel("payment-order-started") Emitter<PaymentOrderStartedEvent> paymentOrderStartedEventEmitter,
      @Channel("transaction-failed") Emitter<TransactionProcessedEvent> transactionProcessedEmitter,
      @Channel("transaction-approved") Emitter<TransactionProcessedEvent> transactionApprovedEmitter) {
    this.transactionRepository = transactionRepository;
    this.pspRestClient = pspRestClient;
    this.paymentOrderStartedEventEmitter = paymentOrderStartedEventEmitter;
    this.transactionFailedEmitter = transactionProcessedEmitter;
    this.transactionApprovedEmitter = transactionApprovedEmitter;
  }
  @Transactional
  void paymentOrderReceived(@Observes PaymentOrderReceived paymentOrder){
    var transactionReceived = Transaction.newTransactionReceived(new PaymentOrderId(paymentOrder.id()),paymentOrder.amount(),paymentOrder.currency(),paymentOrder.checkout()
        .getBuyerInfo(),paymentOrder.checkout().getCardInfo());
    this.transactionRepository.persist(transactionReceived);
    this.paymentOrderStartedEventEmitter.send(new PaymentOrderStartedEvent(paymentOrder.id().toString()));
    var paymentResult = this.pspRestClient.pay(new PaymentRequest(paymentOrder.amount()));
    var transactionProcessed = Transaction.newTransactionProcessed(new PaymentOrderId(paymentOrder.id()),paymentOrder.amount(),paymentOrder.currency(),paymentOrder.checkout()
        .getBuyerInfo(),paymentOrder.checkout().getCardInfo(),paymentResult.getStatus());
    this.transactionRepository.persist(transactionProcessed);
    var event = new TransactionProcessedEvent(new TransactionId(transactionProcessed.getId()),paymentOrder.seller(),new PaymentOrderId(paymentOrder.id()),new CheckoutId(paymentOrder.checkout().getId()),paymentOrder.amount(),paymentOrder.currency(),
        LocalDateTime.now(),paymentOrder.checkout().getBuyerInfo());
    if (TransactionStatus.APPROVED.equals(transactionProcessed.getStatus())){
        this.transactionApprovedEmitter.send(event);
    }else {
        this.transactionFailedEmitter.send(event);
    }
  }

}
