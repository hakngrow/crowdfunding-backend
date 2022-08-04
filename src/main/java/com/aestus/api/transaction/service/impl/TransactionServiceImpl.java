package com.aestus.api.transaction.service.impl;

import com.aestus.api.transaction.model.Transaction;
import com.aestus.api.transaction.repository.TransactionRepository;
import com.aestus.api.transaction.service.TransactionService;

import com.google.common.collect.Iterables;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The implementation of the Transaction service. This implementation uses a {@code CrudRepository}
 * for persistence to a RDBMS via Hibernate.
 */
@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {
  private final TransactionRepository transactionRepository;

  public Iterable<Transaction> getAllTransactions() {
    return transactionRepository.findAll();
  }

  public Iterable<Transaction> getTransactionsByReceiverWalletId(String walletId) {
    return transactionRepository.findByReceiverWalletId(walletId);
  }

  public Iterable<Transaction> getTransactionsBySenderWalletId(String walletId) {
    return transactionRepository.findBySenderWalletId(walletId);
  }

  public Iterable<Transaction> getTransactionsByWalletId(String walletId) {
    return transactionRepository.findByWalletId(walletId);
  }

  public Optional<Transaction> getTransactionById(long id) {
    return transactionRepository.findById(id);
  }

  public Transaction createTransaction(Transaction transaction) {
    return transactionRepository.save(transaction);
  }

  public Transaction updateTransaction(Transaction transaction) {
    return transactionRepository.save(transaction);
  }

  public void deleteTransactionById(long id) {
    transactionRepository.deleteById(id);
  }

  public void deleteAllTransactions() {
    transactionRepository.deleteAll();
  }
}
