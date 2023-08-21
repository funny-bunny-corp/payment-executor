package com.paymentic.domain.psp;

public class PaymentRequest {
  private String value;

  public PaymentRequest(){}

  public PaymentRequest(String value) {
    this.value = value;
  }
  public String getValue() {
    return value;
  }

}
