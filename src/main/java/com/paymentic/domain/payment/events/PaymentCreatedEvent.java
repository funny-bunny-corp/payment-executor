package com.paymentic.domain.payment.events;

import com.paymentic.domain.payment.PaymentTransaction;

public record PaymentCreatedEvent(String status,RiskLevel level, PaymentTransaction transaction) {}
