package com.paymentic.domain.shared;

import jakarta.persistence.Embeddable;

@Embeddable
public class BuyerInfo {
  private String document;
  private String name;

  public BuyerInfo() {
  }
  public BuyerInfo(String document, String name) {
    this.document = document;
    this.name = name;
  }
  public String getDocument() {
    return document;
  }
  public String getName() {
    return name;
  }

}
