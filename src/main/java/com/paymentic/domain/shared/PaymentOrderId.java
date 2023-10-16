package com.paymentic.domain.shared;

import jakarta.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public class PaymentOrderId {
  private UUID id;
  public PaymentOrderId(){}
  public PaymentOrderId(UUID id) {
    this.id = id;
  }
  public UUID getId() {
    return id;
  }
}
