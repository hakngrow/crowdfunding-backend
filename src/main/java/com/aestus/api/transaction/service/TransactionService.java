package com.aestus.api.transaction.service;

import com.aestus.api.transaction.model.Transaction;

import java.util.Optional;

/**
 * The TransactionService interface provides access to application functionality and features for
 * transactions. It acts as a proxy or endpoint to the service. The specific implementation can be
 * found in the {@code TransactionServiceImpl} class in the {@code impl} package.
 */
public interface TransactionService {
  /**
   * Gets all transactions.
   *
   * @return all transactions
   */
  Iterable<Transaction> getAllTransactions();

  /**
   * Gets transactions by receiver wallet id.
   *
   * @param walletId the receiver wallet id
   * @return the list of transactions
   */
  Iterable<Transaction> getTransactionsByReceiverWalletId(String walletId);

  /**
   * Gets transactions by sender wallet id.
   *
   * @param walletId the sender wallet id
   * @return the list of transactions
   */
  Iterable<Transaction> getTransactionsBySenderWalletId(String walletId);

  /**
   * Gets all (sending and receiving) transactions by wallet id.
   *
   * @param walletId the wallet id
   * @return the list of sending and receiving transactions
   */
  Iterable<Transaction> getTransactionsByWalletId(String walletId);

  /**
   * Gets transaction by id.
   *
   * @param id the transaction id
   * @return the transaction if found
   */
  Optional<Transaction> getTransactionById(long id);

  /**
   * Creates a transaction.
   *
   * @param transaction the transaction
   * @return the transaction with generated id
   */
  Transaction createTransaction(Transaction transaction);

  /**
   * Updates the transaction.
   *
   * @param transaction the transaction to be updated
   * @return the updated transaction
   */
  Transaction updateTransaction(Transaction transaction);

  /**
   * Delete transaction by id.
   *
   * @param id the id
   */
  void deleteTransactionById(long id);

  /** Delete all transactions. */
  void deleteAllTransactions();
}
