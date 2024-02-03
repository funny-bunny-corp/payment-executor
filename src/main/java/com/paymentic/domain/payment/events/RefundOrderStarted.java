package com.paymentic.domain.payment.events;

import com.paymentic.domain.shared.SellerInfo;
import java.time.LocalDate;
import org.apache.kafka.common.protocol.types.Field.Str;

public record RefundOrderStarted(String id, String amount, String currency, SellerInfo sellerInfo,
                                 String at) {}
