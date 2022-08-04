package com.aestus.api.request.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.aestus.api.product.model.Product;
import com.aestus.api.profile.model.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** The type Request for proposal. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestForProposal extends Request {

  /** The product the request for proposal is based on */
  protected Product product;

  /** The list of proposals from solution providers */
  protected List<Request> proposals;

  /**
   * Instantiates a new Request for proposal.
   *
   * @param id the id
   * @param fromProfileId the from profile id
   * @param toProfileId the to profile id
   * @param productId the product id on which the RFP is based on
   * @param title the title
   * @param status the status
   * @param description the description
   * @param cost the cost amount
   * @param specifications the specifications
   * @param createdTimestamp the created timestamp
   * @param proposals the proposals
   */
  public RequestForProposal(
      Integer id,
      Integer fromProfileId,
      Integer toProfileId,
      Integer productId,
      String title,
      String status,
      String description,
      Long cost,
      String specifications,
      LocalDateTime createdTimestamp,
      List<Request> proposals,
      UserProfile fromUser) {
    super(
        id,
        fromProfileId,
        toProfileId,
        productId,
        title,
        Request.TYPE_RFP,
        status,
        description,
        cost,
        null,
        specifications,
        createdTimestamp,
        fromUser,
        null);

    this.proposals = proposals;
  }

  /**
   * Instantiates a new request for proposal from a generic {@code Request} instance.
   *
   * @param request the generic request instance
   */
  public RequestForProposal(Request request) {
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
        new ArrayList<Request>(),
        null);
  }

  public Integer getProductId() {
    return this.getRequestId();
  }

  public void setProductId(Integer productId) {
    this.setProductId(productId);
  }

  public UserProfile getFromUser() {
    return this.getFromProfile();
  }

  public void setFromUser(UserProfile user) {
    this.setFromProfile(user);
  }

  public UserProfile getToProvider() {
    return this.getToProfile();
  }

  public void setToProvider(UserProfile provider) {
    this.setToProfile(provider);
  }
}
