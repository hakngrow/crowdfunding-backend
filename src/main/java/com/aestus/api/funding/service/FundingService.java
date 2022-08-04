package com.aestus.api.funding.service;

import com.aestus.api.common.exception.EntityNotFoundException;
import com.aestus.api.funding.model.Funding;

import java.util.List;

/**
 * The FundingService interface provides access to application functionality and features for
 * fundings. It acts as a proxy or endpoint to the service. The specific implementation can be found
 * in the {@code FundingServiceImpl} class in the {@code impl} package.
 */
public interface FundingService {

  /**
   * Gets all fundings.
   *
   * @return all fundings
   */
  Iterable<Funding> getAllFundings();

  /**
   * Gets fundings by {@code contractId}.
   *
   * @param contractId the id of the contract
   * @return the fundings
   */
  Iterable<Funding> getFundingsByContractId(int contractId);

  /**
   * Gets fundings by {@code contractId}.
   *
   * @param profileId the id of the investor profile
   * @return the fundings
   */
  Iterable<Funding> getFundingsByProfileId(int profileId);

  /**
   * Gets funding by id.
   *
   * @param id the funding id
   * @return the funding if found
   * @throws EntityNotFoundException the funding with {@code id} is not found exception
   */
  Funding getFundingById(int id) throws EntityNotFoundException;

  /**
   * Creates a funding.
   *
   * @param funding the funding
   * @return the funding with generated id
   */
  Funding createFunding(Funding funding);

  /**
   * Updates the funding.
   *
   * @param funding the funding to be updated
   * @return the updated Funding
   * @throws EntityNotFoundException the funding with {@code id} is not found
   */
  Funding updateFunding(Funding funding) throws EntityNotFoundException;

  /**
   * Update the status of the funding identified by {@code id}.
   *
   * @param id the funding id
   * @param status the status
   * @throws EntityNotFoundException the funding with {@code id} is not found
   */
  void updateFundingStatus(Integer id, String status) throws EntityNotFoundException;

  /**
   * Update the disbursed amount of the funding identified by {@code id}.
   *
   * @param id the funding id
   * @param amount the disbursed amount to be updated
   * @throws EntityNotFoundException the funding with {@code id} is not found
   */
  void updateFundingDisbursedAmount(Integer id, Long amount) throws EntityNotFoundException;

  /**
   * Disbursed a list of fundings
   *
   * @param ids the list of funding ids to be disbursed
   * @throws EntityNotFoundException the funding with {@code id} is not found
   */
  void disburseFundings(List<Integer> ids) throws EntityNotFoundException;

  /**
   * Delete funding by id.
   *
   * @param id the id
   * @throws EntityNotFoundException the funding with {@code id} is not found
   */
  void deleteFundingById(int id) throws EntityNotFoundException;

  /** Delete all fundings. */
  void deleteAllFundings();
}
