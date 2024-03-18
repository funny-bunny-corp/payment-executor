package com.paymentic.domain.transaction.listeners;

import com.paymentic.domain.payment.RefundReceived;
import com.paymentic.domain.payment.events.RefundOrderStarted;
import com.paymentic.domain.shared.CheckoutId;
import com.paymentic.domain.shared.PaymentOrderId;
import com.paymentic.domain.shared.RefundId;
import com.paymentic.domain.transaction.Transaction;
import com.paymentic.domain.transaction.TransactionId;
import com.paymentic.domain.transaction.TransactionSituation;
import com.paymentic.domain.transaction.TransactionStatus;
import com.paymentic.domain.transaction.TransactionType;
import com.paymentic.domain.transaction.events.TransactionProcessedEvent;
import com.paymentic.domain.transaction.repositories.TransactionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.apache.kafka.common.protocol.types.Field.Str;
import org.jboss.logging.Logger;

@ApplicationScoped
public class RefundReceivedListener {
  private static final Logger LOGGER = Logger.getLogger(RefundReceivedListener.class);
  private final TransactionRepository transactionRepository;
  private final Event<TransactionProcessedEvent> transactionTrigger;
  private final Event<RefundOrderStarted> refundStartedTrigger;
  public RefundReceivedListener(TransactionRepository transactionRepository,
      Event<TransactionProcessedEvent> trigger,
      Event<RefundOrderStarted> refundStartedTrigger) {
    this.transactionRepository = transactionRepository;
    this.transactionTrigger = trigger;
    this.refundStartedTrigger = refundStartedTrigger;
  }
  @Transactional
  void refundReceived(@Observes RefundReceived refundReceived){
    LOGGER.info(String.format("Refund %s received starting process....",refundReceived.refund().id()));
    var transactionReceived = Transaction.newTransactionReceived(new PaymentOrderId(UUID.fromString(refundReceived.paymentOrder().id()))
        ,refundReceived.refund().amount(),refundReceived.refund().currency(),refundReceived.refund().buyerInfo(),refundReceived.refund().cardInfo(),
        TransactionType.REFUND);
    this.transactionRepository.persist(transactionReceived);
    LOGGER.info(String.format("Triggering refund %s started..",refundReceived.refund().id()));
    this.refundStartedTrigger.fire(new RefundOrderStarted(refundReceived.refund().id().toString(),refundReceived.refund().amount(),refundReceived.refund().currency(),refundReceived.refund()
        .sellerInfo(), LocalDate.now().toString()));
    LOGGER.info(String.format("Refund started %s fired!!!", refundReceived.refund().id()));
    var transactionProcessed = Transaction.newTransactionProcessed(new PaymentOrderId(UUID.fromString(refundReceived.paymentOrder().id())),refundReceived.refund().amount(),
        refundReceived.refund().currency(),refundReceived.refund().buyerInfo(),refundReceived.refund().cardInfo(),
        TransactionStatus.APPROVED.name() ,TransactionType.REFUND);
    this.transactionRepository.persist(transactionProcessed);
    var event = TransactionProcessedEvent.ofRefund(new TransactionId(transactionProcessed.getId()),refundReceived.refund()
            .sellerInfo(), new PaymentOrderId(UUID.fromString(refundReceived.paymentOrder().id())),new RefundId(refundReceived.refund().id()),refundReceived.refund().amount(),refundReceived.refund().currency(),
        LocalDateTime.now(),refundReceived.refund().buyerInfo(),transactionProcessed.getStatus());
    this.transactionTrigger.fire(event);
    LOGGER.info(String.format("Refund %s processed successfully!!!",refundReceived.refund().id()));
  }

}
