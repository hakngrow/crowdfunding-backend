package com.aestus.api.contract.service.impl;

import com.aestus.api.common.exception.EntityNotFoundException;
import com.aestus.api.contract.exception.ContractAmountsException;
import com.aestus.api.contract.exception.RequestIdNotFoundException;
import com.aestus.api.contract.model.Contract;
import com.aestus.api.contract.service.ContractService;
import com.aestus.api.contract.exception.ContractException;
import com.aestus.api.contract.repository.ContractRepository;
import com.aestus.api.funding.model.Funding;

import java.util.Optional;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * The implementation of the Contract service. This implementation uses a {@code CrudRepository} for
 * persistence to a RDBMS via Hibernate.
 */
@Service
@AllArgsConstructor
public class ContractServiceImpl implements ContractService {

  private final ContractRepository contractRepository;

  public Iterable<Contract> getAllContracts() {
    return contractRepository.findAll();
  }

  public Contract getContractById(int id) throws EntityNotFoundException {

    Optional<Contract> optContract = contractRepository.findById(id);

    if (optContract.isPresent()) {
      return optContract.get();
    } else throw new EntityNotFoundException(Contract.class, id);
  }

  public Contract getContractByRequestId(int requestId) throws RequestIdNotFoundException {

    Optional<Contract> optContract = contractRepository.findByRequestId(requestId);

    if (optContract.isPresent()) {
      return optContract.get();
    } else throw new RequestIdNotFoundException(requestId);
  }

  public Contract createContract(Contract contract) throws ContractException {

    if (contract.getTargetAmount() >= contract.getRepaymentAmount())
      throw new ContractAmountsException(contract.getTargetAmount(), contract.getRepaymentAmount());

    return contractRepository.save(contract);
  }

  public Contract updateContract(Contract contract) throws EntityNotFoundException {
    getContractById(contract.getId());

    return contractRepository.save(contract);
  }

  public Funding fundContract(Contract contract, int profileId, long fundingAmount)
      throws ContractException, EntityNotFoundException {

    Funding funding = contract.fund(profileId, fundingAmount);

    updateContractStatus(contract.getId(), contract.getStatus());

    return funding;
  }

  public void updateContractStatus(Integer id, String status) throws EntityNotFoundException {
    getContractById(id);
    contractRepository.updateStatus(id, status);
  }

  public void deleteContractById(int id) throws EntityNotFoundException {
    getContractById(id);
    contractRepository.deleteById(id);
  }

  public void deleteAllContracts() {
    contractRepository.deleteAll();
  }
}
