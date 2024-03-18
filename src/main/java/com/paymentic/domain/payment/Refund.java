package com.paymentic.domain.payment;

import com.paymentic.domain.shared.BuyerInfo;
import com.paymentic.domain.shared.CardInfo;
import com.paymentic.domain.shared.SellerInfo;
import java.util.UUID;

public record Refund(UUID id, String amount, String currency, CardInfo cardInfo, BuyerInfo buyerInfo,
                     SellerInfo sellerInfo){}
