package com.paymentic.domain.payment;

import com.paymentic.domain.shared.SellerInfo;

public record PaymentOrder(String id, String amount, String currency, PaymentOrderStatus status,
                           SellerInfo sellerInfo, String idempotencyKey) { }
