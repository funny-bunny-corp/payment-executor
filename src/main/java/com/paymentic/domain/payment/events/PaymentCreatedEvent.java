package com.paymentic.domain.payment.events;

import com.paymentic.domain.checkout.Checkout;
import com.paymentic.domain.payment.PaymentOrder;
import java.util.List;

public record PaymentCreatedEvent(Checkout checkout, List<PaymentOrder> payments) {}
