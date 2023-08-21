package com.paymentic.domain.transaction.repositories;

import com.paymentic.domain.transaction.Transaction;
import com.paymentic.domain.transaction.TransactionId;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransactionRepository implements PanacheRepository<Transaction> {
  public TransactionId store(Transaction transaction){
    this.persist(transaction);
    return new TransactionId(transaction.getId());
  }

}
