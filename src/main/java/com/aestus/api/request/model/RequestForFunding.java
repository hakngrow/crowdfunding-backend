package com.aestus.api.request.model;

import com.aestus.api.contract.model.Contract;
import com.aestus.api.profile.model.UserProfile;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

/** The type Request for funding. */
@Data
@NoArgsConstructor
public class RequestForFunding extends Request {

  /** The proposal the request for funding is based on */
  private Request proposal;

  /** The contract the request for funding is associated to */
  private Contract contract;

  /**
   * Instantiates a new Request for funding.
   *
   * @param id the id
   * @param fromProfileId the from profile id
   * @param proposalId the request id of the proposal
   * @param title the title
   * @param status the status
   * @param description the description
   * @param cost the cost amount
   * @param repayment the repayment amount
   * @param specifications the specifications
   * @param createdTimestamp the created timestamp
   * @param proposal the proposal that the request for funding is based on
   * @param contract the contract that is dervied from the request for funding
   * @param fromUser the from user
   */
  public RequestForFunding(
      Integer id,
      Integer fromProfileId,
      Integer proposalId,
      String title,
      String status,
      String description,
      Long cost,
      Long repayment,
      String specifications,
      LocalDateTime createdTimestamp,
      UserProfile fromUser,
      Request proposal,
      Contract contract) {
    super(
        id,
        fromProfileId,
        0,
        proposalId,
        title,
        Request.TYPE_RFF,
        status,
        description,
        cost,
        repayment,
        specifications,
        createdTimestamp,
        fromUser,
        null);

    this.proposal = proposal;
    this.contract = contract;
  }

  /**
   * Instantiates a new request for funding from a generic {@code Request} instance.
   *
   * @param request the generic request instance
   */
  public RequestForFunding(Request request) {
    this(
        request.getId(),
        request.getFromProfileId(),
        request.getRequestId(),
        request.getTitle(),
        request.getStatus(),
        request.getDescription(),
        request.getCost(),
        request.getRepayment(),
        request.getSpecifications(),
        request.getCreatedTimestamp(),
        null,
        null,
        null);
  }

  public UserProfile getFromUser() {
    return this.getFromProfile();
  }

  public void setFromUser(UserProfile user) {
    this.setFromProfile(user);
  }
}
