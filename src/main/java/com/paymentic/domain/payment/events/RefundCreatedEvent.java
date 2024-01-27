package com.paymentic.domain.payment.events;

import com.paymentic.domain.payment.PaymentOrder;
import com.paymentic.domain.payment.Refund;

public record RefundCreatedEvent(Refund refund,PaymentOrder payment) {}
