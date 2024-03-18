package com.paymentic.domain.payment;

import com.paymentic.domain.shared.BuyerInfo;
import com.paymentic.domain.shared.SellerInfo;

public record Participants(BuyerInfo buyer, SellerInfo seller) { }
