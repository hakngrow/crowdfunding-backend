package com.aestus.api.request.service;

import com.aestus.api.request.model.Proposal;
import com.aestus.api.common.exception.EntityNotFoundException;
import com.aestus.api.request.exception.InvalidRequestTypeException;
import com.aestus.api.request.exception.RequestException;
import com.aestus.api.request.model.Request;
import com.aestus.api.request.model.RequestForFunding;
import com.aestus.api.request.model.RequestForProposal;

import java.util.List;

/**
 * The RequestService interface provides access to application functionality and features for
 * funding requests. It acts as a proxy or endpoint to the service. The specific implementation can
 * be found in the {@code RequestServiceImpl} class in the {@code impl} package.
 */
public interface RequestService {
  /**
   * Gets all requests.
   *
   * @return all requests
   */
  Iterable<Request> getAllRequests();

  /**
   * Gets requests by the {@code fromProfileId}.
   *
   * @param profileId the from profile id
   * @return the requests
   */
  Iterable<Request> getRequestsByFromProfileId(int profileId);

  /**
   * Gets requests by the {@code fromProfileId} and {@code type}.
   *
   * @param profileId the from profile id
   * @param type the request type
   * @return the requests
   */
  Iterable<Request> getRequestsByFromProfileIdAndType(int profileId, String type);

  /**
   * Gets requests by the {@code fromProfileId}, {@code type} and {@code status}.
   *
   * @param profileId the from profile id
   * @param type the request type
   * @param status the request status
   * @return the requests
   */
  Iterable<Request> getRequestsByFromProfileIdAndTypeAndStatus(
      int profileId, String type, String status);

  /**
   * Gets requests by the {@code toProfileId}.
   *
   * @param profileId the to profile id
   * @return the requests
   */
  Iterable<Request> getRequestsByToProfileId(int profileId);

  /**
   * Gets requests by the {@code toProfileId} and {@code type}.
   *
   * @param profileId the to profile id
   * @param type the request type
   * @return the requests
   */
  Iterable<Request> getRequestsByToProfileIdAndType(int profileId, String type);

  /**
   * Gets requests by the {@code toProfileId}, {@code type} and {@code status}.
   *
   * @param profileId the to profile id
   * @param type the request type
   * @param status the request status
   * @return the requests
   */
  Iterable<Request> getRequestsByToProfileIdAndTypeAndStatus(
      int profileId, String type, String status);

  /**
   * Gets requests by matching {@code fromProfileId} and {@code toProfileId}.
   *
   * @param profileId the profile id
   * @return the requests
   */
  Iterable<Request> getRequestsByProfileId(int profileId);

  /**
   * Gets requests by {@code type}.
   *
   * @param type the request type
   * @return the requests
   */
  Iterable<Request> getRequestsByType(String type);

  /**
   * Gets requests by {@code type} and {@code status}.
   *
   * @param type the request type
   * @param status the status of the request
   * @return the requests
   */
  Iterable<Request> getRequestsByTypeAndStatus(String type, String status);

  /**
   * Gets requests by {@code requestId}.
   *
   * @param requestId the related request id
   * @return the requests
   */
  Iterable<Request> getRequestsByRequestId(int requestId);

  /**
   * Gets request for proposals by {@code fromProfileId}.
   *
   * @param fromProfileId the from profile id
   * @return the list of request for proposals
   */
  Iterable<RequestForProposal> getRequestForProposalsFrom(int fromProfileId);

  /**
   * Gets request for proposals by {@code fromProfileId}.
   *
   * @param toProfileId the to profile id
   * @return the list of request for proposals
   */
  Iterable<RequestForProposal> getRequestForProposalsTo(int toProfileId);

  /**
   * Gets proposals by {@code requestId}.
   *
   * @param requestForProposalId the id of the request for proposal
   * @return the list of proposals
   */
  Iterable<Proposal> getProposals(int requestForProposalId)
      throws InvalidRequestTypeException, EntityNotFoundException;

  /**
   * Gets request by id.
   *
   * @param id the request id
   * @return the request if found
   * @throws EntityNotFoundException the request with {@code id} is not found exception
   */
  Request getRequestById(int id) throws EntityNotFoundException;

  /**
   * Gets request for proposal by id.
   *
   * @param id the request id
   * @return the request for proposal if found
   * @throws InvalidRequestTypeException the type is not a request for proposal
   * @throws EntityNotFoundException the request for proposal with {@code id} is not found exception
   */
  RequestForProposal getRequestForProposal(int id)
      throws InvalidRequestTypeException, EntityNotFoundException;

  /**
   * Gets a proposal by id.
   *
   * @param id the proposal id
   * @return the proposal if found
   * @throws InvalidRequestTypeException the type is not a proposal
   * @throws EntityNotFoundException the request for proposal with {@code id} is not found exception
   */
  Proposal getProposal(int id) throws InvalidRequestTypeException, EntityNotFoundException;

  /**
   * Gets the request for fundings from the proposals of provider {@code providerId} which has requested for funds.
   *
   * @param providerId the solution provider id
   * @return the request for funding if
   * @throws InvalidRequestTypeException the type is not a request for funding
   */
  Iterable<RequestForFunding> getRequestForFundingsFor(int providerId)
      throws InvalidRequestTypeException;

  /**
   * Creates a request.
   *
   * @param request the request
   * @return the request with generated id
   */
  Request createRequest(Request request);

  /**
   * Updates the request.
   *
   * @param request the request to be updated
   * @return the updated request
   * @throws EntityNotFoundException the request with {@code request.id} is not found
   */
  Request updateRequest(Request request) throws EntityNotFoundException;

  /**
   * Update the status of the request identified by {@code id}.
   *
   * @param id the id
   * @param status the status
   * @throws EntityNotFoundException the request with {@code id} is not found
   */
  void updateRequestStatus(Integer id, String status) throws EntityNotFoundException;

  /**
   * Update the repayment of the request identified by {@code id}.
   *
   * @param id the id
   * @param repayment the repayment amount
   * @throws EntityNotFoundException the request with {@code id} is not found
   */
  void updateRequestRepayment(Integer id, Long repayment) throws EntityNotFoundException;

  /**
   * Accept a proposal from solution provider.
   *
   * @param proposalId the proposal id
   * @return the list of proposal ids that were rejected as a result of the acceptance
   * @throws EntityNotFoundException the proposal with {@code id} is not found
   * @throws RequestException exception in the {@code type}, {@code state} or {@code requestId} of
   *     the proposal
   */
  List<Integer> acceptProposal(Integer proposalId) throws EntityNotFoundException, RequestException;

  /**
   * Delete request by id.
   *
   * @param id the id
   * @throws EntityNotFoundException the request with {@code id} is not found
   */
  void deleteRequestById(int id) throws EntityNotFoundException;

  /**
   * Delete request by {@code fromProfileId}.
   *
   * @param profileId the from profile id
   */
  List<Request> deleteRequestsByFromProfileId(int profileId);

  /**
   * Delete requests by {@code toProfileId}.
   *
   * @param profileId the to profile id
   */
  List<Request> deleteRequestsByToProfileId(int profileId);

  /**
   * Delete requests by matching {@code fromProfileId} and {@code toProfileId}.
   *
   * @param profileId the profile id
   */
  void deleteRequestsByProfileId(int profileId);

  /** Delete all requests. */
  void deleteAllRequests();
}
