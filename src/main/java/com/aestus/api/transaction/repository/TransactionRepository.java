package com.aestus.api.transaction.repository;

import com.aestus.api.transaction.model.Transaction;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * The TransactionRepository extends the {@code CrudRepository} for basic CRUD operations on a RDBMS via
 * Hibernate.
 */
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    /**
     * Find all transactions from a receiver wallet id.
     *
     * @param receiverWalletId the receiver wallet id
     * @return the list of transactions by receiver wallet id
     */
    List<Transaction> findByReceiverWalletId(String receiverWalletId);

    /**
     * Find all transactions from a sender wallet id.
     *
     * @param senderWalletId the sender wallet id
     * @return the list of transactions by sender wallet id
     */
    List<Transaction> findBySenderWalletId(String senderWalletId);

    @Query("SELECT t FROM Transaction t WHERE t.senderWalletId = :walletId OR t.receiverWalletId = :walletId")
    List<Transaction> findByWalletId(@Param("walletId") String walletId);
}
