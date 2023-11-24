package com.paymentic.adapter.http;

import com.paymentic.domain.psp.PaymentRequest;
import com.paymentic.domain.psp.PaymentResult;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/payments")
@RegisterRestClient
public interface PspRestClient {
  @POST
  PaymentResult pay(PaymentRequest request);



}
