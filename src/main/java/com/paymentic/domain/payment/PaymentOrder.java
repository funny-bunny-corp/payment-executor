package com.paymentic.domain.payment;

import com.paymentic.domain.shared.SellerInfo;
import java.util.UUID;

public record PaymentOrder(UUID id, String amount, String currency, PaymentOrderStatus status,
                           SellerInfo sellerInfo) { }
