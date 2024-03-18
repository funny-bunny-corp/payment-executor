package com.paymentic.domain.checkout;

import com.paymentic.domain.shared.BuyerInfo;
import com.paymentic.domain.shared.CardInfo;
import java.time.LocalDateTime;
import java.util.UUID;

public class Checkout {
  private UUID id;
  private CardInfo cardInfo;
  private LocalDateTime at;
  public Checkout() {
  }
  private Checkout(UUID id, CardInfo cardInfo,LocalDateTime at) {
    this.cardInfo = cardInfo;
    this.id = id;
    this.at = at;
  }
  public UUID getId() {
    return id;
  }
  public CardInfo getCardInfo() {
    return cardInfo;
  }
  public LocalDateTime getAt() {
    return at;
  }
}
