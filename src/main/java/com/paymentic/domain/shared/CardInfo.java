package com.paymentic.domain.shared;

import jakarta.persistence.Embeddable;

@Embeddable
public class CardInfo {
  private String cardInfo;
  private String token;

  public CardInfo() {
  }
  public CardInfo(String cardInfo, String token) {
    this.cardInfo = cardInfo;
    this.token = token;
  }
  public String getCardInfo() {
    return cardInfo;
  }
  public String getToken() {
    return token;
  }

}
