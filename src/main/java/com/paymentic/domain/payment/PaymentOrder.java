package com.paymentic.domain.payment;

import java.util.UUID;

public record PaymentOrder(UUID id, String amount, String currency, PaymentOrderStatus status) { }
