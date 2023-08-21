package com.paymentic.domain.psp;

import java.time.LocalDateTime;

public class PaymentResult {
  private String id;
  private String status;
  private LocalDateTime at;
  public PaymentResult() {
  }
  public PaymentResult(String id, String status, LocalDateTime at) {
    this.id = id;
    this.status = status;
    this.at = at;
  }
  public String getId() {
    return id;
  }
  public String getStatus() {
    return status;
  }
  public LocalDateTime getAt() {
    return at;
  }

}
