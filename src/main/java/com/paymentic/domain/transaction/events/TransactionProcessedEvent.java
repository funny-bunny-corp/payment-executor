package com.paymentic.domain.transaction.events;

import com.paymentic.domain.shared.BuyerInfo;
import com.paymentic.domain.shared.CheckoutId;
import com.paymentic.domain.shared.PaymentOrderId;
import com.paymentic.domain.shared.SellerInfo;
import com.paymentic.domain.transaction.TransactionId;
import com.paymentic.domain.transaction.TransactionStatus;
import java.time.LocalDateTime;

public record TransactionProcessedEvent(TransactionId transaction, SellerInfo seller,
                                        PaymentOrderId payment, CheckoutId checkoutId, String amount, String currency,
                                        LocalDateTime at, BuyerInfo buyer, TransactionStatus status) {}
