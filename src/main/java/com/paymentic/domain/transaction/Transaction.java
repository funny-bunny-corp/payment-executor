package com.paymentic.domain.transaction;

import com.paymentic.domain.shared.BuyerInfo;
import com.paymentic.domain.shared.CardInfo;
import com.paymentic.domain.shared.PaymentOrderId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

@Entity(name = "transaction")
public class Transaction {

  @Id
  @Column(name = "transaction_id")
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID id;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name="id",column=@Column(name="payment_order_id"))
  })
  private PaymentOrderId paymentOrder;
  private String amount;
  private String currency;
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name="document",column=@Column(name="buyer_info_document")),
      @AttributeOverride(name="name",column=@Column(name="buyer_info_name"))
  })
  private BuyerInfo buyerInfo;
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name="cardInfo",column=@Column(name="card_info_info")),
      @AttributeOverride(name="token",column=@Column(name="card_info_token"))
  })
  private CardInfo cardInfo;
  @Column(name = "created_at")
  private LocalDateTime createdAt;
  @Enumerated(value = EnumType.STRING)
  private TransactionStatus status;
  @Enumerated(value = EnumType.STRING)
  private TransactionSituation situation;

  public Transaction(){}
  public Transaction(PaymentOrderId paymentOrder, String amount, String currency,
      BuyerInfo buyerInfo,
      CardInfo cardInfo, LocalDateTime createdAt, TransactionStatus status,TransactionSituation situation) {
    this.paymentOrder = paymentOrder;
    this.amount = amount;
    this.currency = currency;
    this.buyerInfo = buyerInfo;
    this.cardInfo = cardInfo;
    this.createdAt = createdAt;
    this.status = status;
    this.situation = situation;
  }
  public static Transaction newTransactionReceived(PaymentOrderId paymentOrder, String amount, String currency,
      BuyerInfo buyerInfo, CardInfo cardInfo){
    return new Transaction(paymentOrder,amount,currency,buyerInfo,cardInfo,LocalDateTime.now(),TransactionStatus.UNDEFINED,TransactionSituation.RECEIVED);
  }

  public static Transaction newTransactionProcessed(PaymentOrderId paymentOrder, String amount, String currency,
      BuyerInfo buyerInfo, CardInfo cardInfo,String result){
    return new Transaction(paymentOrder,amount,currency,buyerInfo,cardInfo,LocalDateTime.now(),TransactionStatus.valueOf(result.toUpperCase()),TransactionSituation.PROCESSED);
  }

  public UUID getId() {
    return id;
  }
  public PaymentOrderId getPaymentOrder() {
    return paymentOrder;
  }
  public String getAmount() {
    return amount;
  }
  public String getCurrency() {
    return currency;
  }
  public BuyerInfo getBuyerInfo() {
    return buyerInfo;
  }
  public CardInfo getCardInfo() {
    return cardInfo;
  }
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
  public TransactionStatus getStatus() {
    return status;
  }

}
