package com.aestus.api.funding.service.impl;

import com.aestus.api.common.exception.EntityNotFoundException;
import com.aestus.api.funding.model.Funding;
import com.aestus.api.funding.repository.FundingRepository;
import com.aestus.api.funding.service.FundingService;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * The implementation of the Funding service. This implementation uses a {@code CrudRepository} for
 * persistence to a RDBMS via Hibernate.
 */
@Service
@AllArgsConstructor
public class FundingServiceImpl implements FundingService {

  private final FundingRepository fundingRepository;

  public Iterable<Funding> getAllFundings() {
    return fundingRepository.findAll();
  }

  public Iterable<Funding> getFundingsByContractId(int contractId) {
    return fundingRepository.findByContractId(contractId);
  }

  public Iterable<Funding> getFundingsByProfileId(int profileId) {
    return fundingRepository.findByProfileId(profileId);
  }

  public Funding getFundingById(int id) throws EntityNotFoundException {

    Optional<Funding> optFunding = fundingRepository.findById(id);

    if (optFunding.isPresent()) {
      return optFunding.get();
    } else throw new EntityNotFoundException(Funding.class, id);
  }

  public Funding createFunding(Funding funding) {
    return fundingRepository.save(funding);
  }

  public Funding updateFunding(Funding contract) throws EntityNotFoundException {
    getFundingById(contract.getId());

    return fundingRepository.save(contract);
  }

  public void updateFundingStatus(Integer id, String status) throws EntityNotFoundException {
    getFundingById(id);
    fundingRepository.updateStatus(id, status);
  }

  public void updateFundingDisbursedAmount(Integer id, Long amount) throws EntityNotFoundException {
    getFundingById(id);
    fundingRepository.updateDisbursedAmount(id, amount);
  }

  public void disburseFundings(List<Integer> ids) throws EntityNotFoundException {

    for (Integer id : ids) {

      Funding funding = getFundingById(id);

      updateFundingStatus(id, Funding.STATUS_FUNDS_DISBURSED);
      updateFundingDisbursedAmount(id, funding.getRepaymentAmount());
    }
  }

  public void deleteFundingById(int id) throws EntityNotFoundException {
    getFundingById(id);
    fundingRepository.deleteById(id);
  }

  public void deleteAllFundings() {
    fundingRepository.deleteAll();
  }
}
