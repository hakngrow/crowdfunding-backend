package com.aestus.api.request.service.impl;

import com.aestus.api.request.exception.*;
import com.aestus.api.request.model.Proposal;
import com.aestus.api.common.exception.EntityNotFoundException;
import com.aestus.api.request.model.Request;
import com.aestus.api.request.model.RequestForFunding;
import com.aestus.api.request.model.RequestForProposal;
import com.aestus.api.request.repository.RequestRepository;
import com.aestus.api.request.service.RequestService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

/**
 * The implementation of the Request service. This implementation uses a {@code CrudRepository} for
 * persistence to a RDBMS via Hibernate.
 */
@Slf4j
@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
  private final RequestRepository requestRepository;

  public Iterable<Request> getAllRequests() {
    return requestRepository.findAll();
  }

  public Iterable<Request> getRequestsByFromProfileId(int profileId) {
    return requestRepository.findByFromProfileId(profileId);
  }

  public Iterable<Request> getRequestsByFromProfileIdAndType(int profileId, String type) {
    return requestRepository.findByFromProfileIdAndType(profileId, type);
  }

  public Iterable<Request> getRequestsByFromProfileIdAndTypeAndStatus(
      int profileId, String type, String status) {
    return requestRepository.findByFromProfileIdAndTypeAndStatus(profileId, type, status);
  }

  public Iterable<Request> getRequestsByToProfileId(int profileId) {
    return requestRepository.findByToProfileId(profileId);
  }

  public Iterable<Request> getRequestsByToProfileIdAndType(int profileId, String type) {
    return requestRepository.findByToProfileIdAndType(profileId, type);
  }

  public Iterable<Request> getRequestsByToProfileIdAndTypeAndStatus(
      int profileId, String type, String status) {
    return requestRepository.findByToProfileIdAndTypeAndStatus(profileId, type, status);
  }

  public Iterable<Request> getRequestsByProfileId(int profileId) {
    return requestRepository.findByProfileId(profileId);
  }

  public Iterable<Request> getRequestsByType(String type) {
    return requestRepository.findByType(type);
  }

  public Iterable<Request> getRequestsByTypeAndStatus(String type, String status) {
    return requestRepository.findByTypeAndStatus(type, status);
  }

  public Iterable<Request> getRequestsByRequestId(int requestId) {
    return requestRepository.findByRequestId(requestId);
  }

  public Iterable<RequestForProposal> getRequestForProposalsFrom(int profileId) {
    Iterable<Request> requests =
        requestRepository.findByFromProfileIdAndType(profileId, Request.TYPE_RFP);

    ArrayList<RequestForProposal> rfps = new ArrayList<RequestForProposal>();

    for (Request request : requests) rfps.add(new RequestForProposal(request));

    return rfps;
  }

  public Iterable<RequestForProposal> getRequestForProposalsTo(int profileId) {
    Iterable<Request> requests =
        requestRepository.findByToProfileIdAndType(profileId, Request.TYPE_RFP);

    ArrayList<RequestForProposal> rfps = new ArrayList<RequestForProposal>();

    for (Request request : requests) rfps.add(new RequestForProposal(request));

    return rfps;
  }

  public Iterable<Proposal> getProposals(int requestForProposalId)
      throws InvalidRequestTypeException, EntityNotFoundException {
    Iterable<Request> requests = requestRepository.findByRequestId(requestForProposalId);

    ArrayList<Proposal> proposals = new ArrayList<Proposal>();

    for (Request request : requests) {
      // Convert generic requests to proposals
      Proposal proposal = getProposal(request.getId());

      proposals.add(proposal);
    }

    return proposals;
  }

  public Request getRequestById(int id) throws EntityNotFoundException {

    Optional<Request> optRequest = requestRepository.findById(id);

    if (optRequest.isPresent()) {
      return optRequest.get();
    } else throw new EntityNotFoundException(Request.class, id);
  }

  public RequestForProposal getRequestForProposal(int id)
      throws InvalidRequestTypeException, EntityNotFoundException {

    Request request = getRequestById(id);

    if (request.isRFP()) {

      RequestForProposal rfp = new RequestForProposal(request);

      return rfp;
    } else throw new InvalidRequestTypeException(request.getType());
  }

  public Proposal getProposal(int id) throws InvalidRequestTypeException, EntityNotFoundException {

    Request request = getRequestById(id);

    if (request.isPRO()) {
      return new Proposal(request);
    } else throw new InvalidRequestTypeException(request.getType());
  }

  public Iterable<RequestForFunding> getRequestForFundingsFor(int providerId)
      throws InvalidRequestTypeException {

    Iterable<Request> proposals =
        getRequestsByFromProfileIdAndTypeAndStatus(
            providerId, Request.TYPE_PRO, Request.STATUS_FUNDING_REQUESTED);

    ArrayList<RequestForFunding> rffs = new ArrayList<RequestForFunding>();

    for (Request proposal : proposals) {
      Iterator<Request> requests = getRequestsByRequestId(proposal.getId()).iterator();

      if (requests.hasNext()) {
        Request request = requests.next();

        if (request.isRFF()) rffs.add(new RequestForFunding(request));
        else throw new InvalidRequestTypeException(request.getType());
      }
    }

    return rffs;
  }

  public Request createRequest(Request request) {
    return requestRepository.save(request);
  }

  public Request updateRequest(Request request) throws EntityNotFoundException {

    getRequestById(request.getId());

    return requestRepository.save(request);
  }

  public void updateRequestStatus(Integer id, String status) throws EntityNotFoundException {

    getRequestById(id);

    requestRepository.updateStatus(id, status);
  }

  public void updateRequestRepayment(Integer id, Long repayment) throws EntityNotFoundException {

    Request request = getRequestById(id);

    requestRepository.updateRepayment(id, repayment);
  }

  public List<Integer> acceptProposal(Integer proposalId)
      throws EntityNotFoundException, RequestException {

    // Retrieve proposal
    Request proposal = getRequestById(proposalId);

    // Check if request type is a proposal aka RFA
    if (proposal.isPRO()) {

      // Check if proposal is still open status
      if (proposal.isOpen()) {

        // Check proposal is linked to a request for proposal
        if (proposal.getRequestId() != null) {

          updateRequestStatus(proposalId, Request.STATUS_ACCEPTED);

          // Retrieve the id of the request for proposal
          int rfpId = proposal.getRequestId();

          // Accepting a proposal will reject all other proposals for the same RFP
          Iterable<Request> proposals = getRequestsByRequestId(rfpId);

          // Get ids of the other proposals
          List<Integer> otherProposalIds =
              StreamSupport.stream(proposals.spliterator(), false)
                  .map(Request::getId)
                  .collect(Collectors.toList());

          otherProposalIds.remove(proposalId);

          // Reject other proposals
          for (Integer id : otherProposalIds) updateRequestStatus(id, Request.STATUS_REJECTED);

          // Update request for proposal to accepted
          updateRequestStatus(rfpId, Request.STATUS_CLOSED);

          return otherProposalIds;
        } else throw new InvalidRequestIdException(proposal.getRequestId());
      } else throw new InvalidRequestStatusException(proposal.getStatus());
    } else throw new InvalidRequestTypeException(proposal.getType());
  }

  public void deleteRequestById(int id) throws EntityNotFoundException {
    getRequestById(id);
    requestRepository.deleteById(id);
  }

  public List<Request> deleteRequestsByFromProfileId(int profileId) {
    return requestRepository.deleteByFromProfileId(profileId);
  }

  public List<Request> deleteRequestsByToProfileId(int profileId) {
    return requestRepository.deleteByToProfileId(profileId);
  }

  public void deleteRequestsByProfileId(int profileId) {
    requestRepository.deleteByProfileId(profileId);
  }

  public void deleteAllRequests() {
    requestRepository.deleteAll();
  }
}
