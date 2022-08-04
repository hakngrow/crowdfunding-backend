package com.aestus.api.contract.service;

import com.aestus.api.common.exception.EntityNotFoundException;
import com.aestus.api.contract.exception.RequestIdNotFoundException;
import com.aestus.api.contract.model.Contract;
import com.aestus.api.contract.exception.ContractException;
import com.aestus.api.funding.model.Funding;

/**
 * The ContractService interface provides access to application functionality and features for
 * contracts. It acts as a proxy or endpoint to the service. The specific implementation can be
 * found in the {@code ContractServiceImpl} class in the {@code impl} package.
 */
public interface ContractService {

  /**
   * Gets all contracts.
   *
   * @return all contracts
   */
  Iterable<Contract> getAllContracts();

  /**
   * Gets contract by id.
   *
   * @param id the contract id
   * @return the contract if found
   * @throws EntityNotFoundException the contract with {@code id} is not found exception
   */
  Contract getContractById(int id) throws EntityNotFoundException;

  /**
   * Gets contract by {@code requestId}.
   *
   * @param requestId the request id
   * @return the contract if found
   * @throws RequestIdNotFoundException the contract with {@code requestId} is not found exception
   */
  Contract getContractByRequestId(int requestId) throws RequestIdNotFoundException;

  /**
   * Creates a contract.
   *
   * @param contract the contract
   * @return the contract with generated id
   * @throws ContractException the amounts of the contract are invalid
   */
  Contract createContract(Contract contract) throws ContractException;

  /**
   * Updates the contract.
   *
   * @param contract the contract to be updated
   * @return the updated Contract
   * @throws EntityNotFoundException the contract with {@code id} is not found
   */
  Contract updateContract(Contract contract) throws EntityNotFoundException;

  /**
   * Update the status of the contract identified by {@code id}.
   *
   * @param id the contract id
   * @param status the status
   * @throws EntityNotFoundException the contract with {@code id} is not found
   */
  void updateContractStatus(Integer id, String status) throws EntityNotFoundException;

  /**
   * Funds the contract identified by {@code contractId}.
   *
   * @param contract the contract to be funded
   * @param profileId the profile id of the user providing the funding
   * @param fundingAmount the amount of the funding
   * @return the funding instance
   * @throws EntityNotFoundException the contract with {@code contractId} is not found
   */
  Funding fundContract(Contract contract, int profileId, long fundingAmount)
      throws ContractException, EntityNotFoundException;

  /**
   * Delete contract by id.
   *
   * @param id the id
   * @throws EntityNotFoundException the contract with {@code id} is not found
   */
  void deleteContractById(int id) throws EntityNotFoundException;

  /** Delete all contracts. */
  void deleteAllContracts();
}
