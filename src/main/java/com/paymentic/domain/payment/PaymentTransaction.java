package com.paymentic.domain.payment;

import com.paymentic.domain.checkout.Checkout;

public record PaymentTransaction(PaymentOrder payment, Checkout order,Participants participants) { }
