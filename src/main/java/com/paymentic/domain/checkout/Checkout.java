package com.paymentic.domain.checkout;

import com.paymentic.domain.shared.BuyerInfo;
import com.paymentic.domain.shared.CardInfo;
import java.util.UUID;

public class Checkout {
  private UUID id;
  private BuyerInfo buyerInfo;
  private CardInfo cardInfo;
  public UUID getId() {
    return id;
  }
  public Checkout() {
  }
  private Checkout(UUID id,BuyerInfo buyerInfo, CardInfo cardInfo) {
    this.buyerInfo = buyerInfo;
    this.cardInfo = cardInfo;
    this.id = id;
  }
  public BuyerInfo getBuyerInfo() {
    return buyerInfo;
  }
  public CardInfo getCardInfo() {
    return cardInfo;
  }

}
