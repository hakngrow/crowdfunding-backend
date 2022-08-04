package com.aestus.api.contract.repository;

import com.aestus.api.contract.model.Contract;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * The ContractRepository extends the {@code CrudRepository} for basic CRUD operations on a RDBMS
 * via Hibernate.
 */
public interface ContractRepository extends CrudRepository<Contract, Integer> {

  @Modifying
  @Transactional
  @Query("UPDATE FROM Contract c SET c.status = :status WHERE c.id = :id")
  void updateStatus(Integer id, String status);
  Optional<Contract> findByRequestId(Integer requestId);
}
