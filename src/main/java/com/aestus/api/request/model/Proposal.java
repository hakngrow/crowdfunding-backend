package com.aestus.api.request.model;

import com.aestus.api.profile.model.UserProfile;

import java.time.LocalDateTime;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** The request type Proposal. */
@Data
@NoArgsConstructor
public class Proposal extends Request {

  /**
   * Instantiates a new Proposal.
   *
   * @param id the id
   * @param fromProfileId the from profile id
   * @param toProfileId the to profile id
   * @param requestForProposalId the request for proposal id
   * @param title the title
   * @param status the status
   * @param description the description
   * @param cost the cost
   * @param specifications the specifications
   * @param createdTimestamp the created timestamp
   * @param fromProvider the profile of the solution provider
   * @param toUser the profile of the user
   */
  public Proposal(
      Integer id,
      Integer fromProfileId,
      Integer toProfileId,
      Integer requestForProposalId,
      String title,
      String status,
      String description,
      Long cost,
      String specifications,
      LocalDateTime createdTimestamp,
      UserProfile fromProvider,
      UserProfile toUser) {
    super(
        id,
        fromProfileId,
        toProfileId,
        requestForProposalId,
        title,
        Request.TYPE_PRO,
        status,
        description,
        cost,
        null,
        specifications,
        createdTimestamp,
        fromProvider,
        toUser);
  }

  /**
   * Instantiates a new proposal from a generic {@code Request} instance.
   *
   * @param request the generic request instance
   */
  public Proposal(Request request) {
    this(
        request.getId(),
        request.getFromProfileId(),
        request.getToProfileId(),
        request.getRequestId(),
        request.getTitle(),
        request.getStatus(),
        request.getDescription(),
        request.getCost(),
        request.getSpecifications(),
        request.getCreatedTimestamp(),
        null,
        null);
  }

  public UserProfile getProvider() {
    return this.getFromProfile();
  }

  public void setProvider(UserProfile provider) {
    this.setFromProfile(provider);
  }

  public UserProfile getUser() {
    return this.getToProfile();
  }

  public void setUser(UserProfile user) {
    this.setToProfile(user);
  }
}
