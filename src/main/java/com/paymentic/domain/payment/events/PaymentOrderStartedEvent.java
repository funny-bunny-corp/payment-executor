package com.paymentic.domain.payment.events;

import com.paymentic.domain.shared.SellerInfo;
import java.time.LocalDate;

public record PaymentOrderStartedEvent(String id, String amount, String currency, SellerInfo sellerInfo,
                                       String at) {}
