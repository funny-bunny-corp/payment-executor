package com.paymentic.domain.shared;

import jakarta.persistence.Embeddable;

@Embeddable
public class SellerInfo {
  private String sellerId;

  public SellerInfo() {
  }
  public SellerInfo(String sellerId) {
    this.sellerId = sellerId;
  }
  public String getSellerId() {
    return sellerId;
  }

}
