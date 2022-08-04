package com.aestus.api.request.model;

import com.aestus.api.profile.model.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * The type Request. There can be different request types indicated by the {@code type} variable
 * e.g. RFP for request for proposal, PRO for proposal, RFF for request for funding
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity()
@Table(
    name = "requests",
    indexes = {
      @Index(name = "idx_fromProfileId_title", columnList = "fromProfileId, title"),
      @Index(name = "idx_toProfileId_title", columnList = "toProfileId, title")
    })
public class Request {

  public static String TYPE_RFP = "RFP"; // Request for proposal
  public static String TYPE_PRO = "PRO"; // Proposal from solution provider
  public static String TYPE_RFF = "RFF"; // Request for funding
  public static String STATUS_OPEN = "O"; // Initial status for all requests
  public static String STATUS_CLOSED = "C"; // Request for proposal closed
  public static String STATUS_ACCEPTED = "ACC"; // Proposal accepted

  public static String STATUS_FUNDING_REQUESTED = "FR"; // Funding (for proposal) requested

  public static String STATUS_SOLUTION_DELIVERED = "SD"; // Solution delivered
  public static String STATUS_SOLUTION_ACCEPTED = "SA"; // Solution accepted
  public static String STATUS_SOLUTION_PAID = "SP"; // Solution accepted

  public static String STATUS_REPAID = "RP"; // Request for funds repaid
  public static String STATUS_FUNDS_DISBURSED = "FD"; // Funds disbursed to investors
  public static String STATUS_REJECTED =
      "REJ"; // Request rejected, can be for proposal or request for funding

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull(message = "fromProfileId must not be null")
  @Column(nullable = false)
  private Integer fromProfileId;

  @NotNull(message = "toProfileId must not be null")
  @Column(nullable = false)
  private Integer toProfileId;

  /**
   * The request id of a related request. For purpose of linking a request for solution and the
   * subsequent request for approval.
   */
  private Integer requestId;

  @NotBlank(message = "title must not be blank")
  @Size(min = 1, max = 300, message = "title must contain between 1 to 300 characters")
  @Column(nullable = false)
  private String title;

  /** The type of the request */
  @NotBlank(message = "type must not be blank")
  @Size(min = 1, max = 10, message = "type must contain between 1 to 10 characters")
  @Column(nullable = false)
  private String type;

  @NotBlank(message = "status must not be blank")
  @Size(min = 1, max = 10, message = "status must contain between 1 to 10 characters")
  @Column(nullable = false)
  private String status;

  @NotBlank(message = "description must not be blank")
  @Size(min = 1, message = "description must contain at least 1 character")
  @Column(nullable = false, columnDefinition = "text")
  private String description;

  @NotNull(message = "cost must not be null")
  @Positive(message = "cost must be positive")
  @Min(value = 1, message = "cost must be at least 1")
  @Column(nullable = false)
  private Long cost;

  @Positive(message = "repayment must be positive")
  private Long repayment;

  @NotBlank(message = "specifications must not be blank")
  @Size(min = 1, message = "specifications must contain at least 1 character")
  @Column(nullable = false, columnDefinition = "text")
  private String specifications;

  @NotNull(message = "createTimestamp must not be Null")
  @Column(nullable = false)
  private LocalDateTime createdTimestamp; // e.g. 2022-05-07T07:53:46.343+00:00

  @Transient
  private UserProfile fromProfile;

  @Transient
  private UserProfile toProfile;

  /**
   * Instantiates a new funding Request.
   *
   * @param fromProfileId the profile id the request originate from
   * @param toProfileId the profile id the request is intended for
   * @param requestId the id of a related request
   * @param title the title of the request
   * @param type the type of the request
   * @param status the status of the request
   * @param description the description of the request
   * @param cost the cost of the request
   * @param repayment the repayment of the request
   * @param specifications the specifications of the request
   * @param createdTimestamp the timestamp the request was submitted
   */
  public Request(
      Integer fromProfileId,
      Integer toProfileId,
      Integer requestId,
      String title,
      String type,
      String status,
      String description,
      Long cost,
      Long repayment,
      String specifications,
      LocalDateTime createdTimestamp) {
    this.fromProfileId = fromProfileId;
    this.toProfileId = toProfileId;
    this.requestId = requestId;
    this.title = title;
    this.type = type;
    this.status = status;
    this.description = description;
    this.cost = cost;
    this.repayment = repayment;
    this.specifications = specifications;
    this.createdTimestamp = createdTimestamp;
  }

  public boolean isRFP() {
    return this.type.equals(TYPE_RFP);
  }
  public boolean isPRO() {
    return this.type.equals(TYPE_PRO);
  }
  public boolean isRFF() {
    return this.type.equals(TYPE_RFF);
  }

  public boolean isOpen() {
    return this.status.equals(STATUS_OPEN);
  }
  public boolean isClosed() {
    return this.status.equals(STATUS_CLOSED);
  }
  public boolean isAccepted() {
    return this.type.equals(STATUS_ACCEPTED);
  }
  public boolean isRejected() {
    return this.status.equals(STATUS_REJECTED);
  }
}
