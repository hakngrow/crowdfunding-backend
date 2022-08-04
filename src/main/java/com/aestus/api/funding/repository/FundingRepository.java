package com.aestus.api.funding.repository;

import com.aestus.api.funding.model.Funding;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * The FundingRepository extends the {@code CrudRepository} for basic CRUD operations on a RDBMS via
 * Hibernate.
 */
public interface FundingRepository extends CrudRepository<Funding, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE FROM Funding f SET f.status = :status WHERE f.id = :id")
    void updateStatus(Integer id, String status);

    @Modifying
    @Transactional
    @Query("UPDATE FROM Funding f SET f.disbursedAmount = :amount WHERE f.id = :id")
    void updateDisbursedAmount(Integer id, Long amount);

    Iterable<Funding> findByContractId(Integer contractId);

    Iterable<Funding> findByProfileId(Integer profileId);
}
