package com.paymentic.domain.transaction.events;

import com.paymentic.domain.shared.BuyerInfo;
import com.paymentic.domain.shared.CheckoutId;
import com.paymentic.domain.shared.PaymentOrderId;
import com.paymentic.domain.shared.RefundId;
import com.paymentic.domain.shared.SellerInfo;
import com.paymentic.domain.transaction.TransactionId;
import com.paymentic.domain.transaction.TransactionStatus;
import com.paymentic.domain.transaction.TransactionType;
import java.time.LocalDateTime;
import java.util.Objects;
import org.apache.kafka.common.protocol.types.Field.Str;

public class TransactionProcessedEvent {
  private final TransactionId transaction;
  private final SellerInfo seller;
  private final PaymentOrderId payment;
  private CheckoutId checkoutId;
  private RefundId refundId;
  private final String amount;
  private final String currency;
  private final LocalDateTime at;
  private final BuyerInfo buyer;
  private final TransactionStatus status;
  public TransactionProcessedEvent(TransactionId transaction, SellerInfo seller, PaymentOrderId payment, CheckoutId checkoutId, String amount, String currency, LocalDateTime at, BuyerInfo buyer, TransactionStatus status) {
    this.transaction = transaction;
    this.seller = seller;
    this.payment = payment;
    this.checkoutId = checkoutId;
    this.amount = amount;
    this.currency = currency;
    this.at = at;
    this.buyer = buyer;
    this.status = status;
  }
  public TransactionProcessedEvent(TransactionId transaction, SellerInfo seller, PaymentOrderId payment, RefundId refundId, String amount, String currency, LocalDateTime at, BuyerInfo buyer, TransactionStatus status) {
    this.transaction = transaction;
    this.seller = seller;
    this.payment = payment;
    this.refundId = refundId;
    this.amount = amount;
    this.currency = currency;
    this.at = at;
    this.buyer = buyer;
    this.status = status;
  }
  public static TransactionProcessedEvent ofCheckout(TransactionId transaction, SellerInfo seller, PaymentOrderId payment, CheckoutId checkoutId, String amount, String currency, LocalDateTime at, BuyerInfo buyer, TransactionStatus status) {
    return new TransactionProcessedEvent(transaction, seller, payment, checkoutId, amount, currency, at, buyer, status);
  }
  public static TransactionProcessedEvent ofRefund(TransactionId transaction, SellerInfo seller, PaymentOrderId payment, RefundId refundId, String amount, String currency, LocalDateTime at, BuyerInfo buyer, TransactionStatus status) {
    return new TransactionProcessedEvent(transaction, seller, payment, refundId, amount, currency, at, buyer, status);
  }
  public TransactionId getTransaction() {
    return transaction;
  }
  public SellerInfo getSeller() {
    return seller;
  }
  public PaymentOrderId getPayment() {
    return payment;
  }
  public CheckoutId getCheckoutId() {
    return checkoutId;
  }
  public String getAmount() {
    return amount;
  }
  public String getCurrency() {
    return currency;
  }
  public LocalDateTime getAt() {
    return at;
  }
  public BuyerInfo getBuyer() {
    return buyer;
  }
  public TransactionStatus getStatus() {
    return status;
  }
  public RefundId getRefundId() {
    return refundId;
  }
  public boolean isRefund(){
    return Objects.nonNull(this.refundId);
  }
  public boolean isCheckout(){
    return Objects.nonNull(this.checkoutId);
  }
  public String type(){
    if (Objects.nonNull(this.refundId)){
      return TransactionType.REFUND.name();
    }
    return TransactionType.PAYMENT.name();
  }
  public String id(){
    if (Objects.nonNull(this.refundId)){
      return this.refundId.id().toString();
    }
    return this.checkoutId.id().toString();
  }

}

