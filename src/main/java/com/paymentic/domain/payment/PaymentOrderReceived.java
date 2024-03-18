package com.paymentic.domain.payment;

import com.paymentic.domain.checkout.Checkout;
import com.paymentic.domain.shared.BuyerInfo;
import com.paymentic.domain.shared.SellerInfo;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentOrderReceived(UUID id, String amount, String currency, PaymentOrderStatus status,
                                   LocalDateTime at, Checkout checkout, SellerInfo seller,
                                   BuyerInfo buyer) { }
