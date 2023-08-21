package com.paymentic.domain.transaction.listeners;

import com.paymentic.adapter.http.PspRestClient;
import com.paymentic.domain.payment.PaymentOrderReceived;
import com.paymentic.domain.psp.PaymentRequest;
import com.paymentic.domain.shared.PaymentOrderId;
import com.paymentic.domain.transaction.Transaction;
import com.paymentic.domain.transaction.repositories.TransactionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class PaymentOrderReceivedListener {
  private final TransactionRepository transactionRepository;
  private final PspRestClient pspRestClient;
  public PaymentOrderReceivedListener(TransactionRepository transactionRepository,
      @RestClient PspRestClient pspRestClient) {
    this.transactionRepository = transactionRepository;
    this.pspRestClient = pspRestClient;
  }

  @Transactional
  void paymentOrderReceived(@Observes PaymentOrderReceived paymentOrder){
    var transactionReceived = Transaction.newTransactionReceived(new PaymentOrderId(paymentOrder.id()),paymentOrder.amount(),paymentOrder.currency(),paymentOrder.checkout()
        .getBuyerInfo(),paymentOrder.checkout().getCardInfo());
    this.transactionRepository.persist(transactionReceived);
    var paymentResult = this.pspRestClient.pay(new PaymentRequest(paymentOrder.amount()));
    var transactionProcessed = Transaction.newTransactionProcessed(new PaymentOrderId(paymentOrder.id()),paymentOrder.amount(),paymentOrder.currency(),paymentOrder.checkout()
        .getBuyerInfo(),paymentOrder.checkout().getCardInfo(),paymentResult.getStatus());
    this.transactionRepository.persist(transactionProcessed);
  }

}
