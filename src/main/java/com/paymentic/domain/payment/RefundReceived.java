package com.paymentic.domain.payment;

import java.util.UUID;
public record RefundReceived(Refund refund,PaymentOrder paymentOrder) { }
