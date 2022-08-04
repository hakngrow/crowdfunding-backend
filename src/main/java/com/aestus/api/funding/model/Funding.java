package com.aestus.api.funding.model;

import com.aestus.api.profile.model.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fundings")
public class Funding {

  public static final String STATUS_FUNDS_IN_CONTRACT = "FIC"; // Funds in contract
  public static final String STATUS_FUNDS_DISBURSED = "FD"; // Funds disbursed

  /** The Id. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  /** The id of the contract. */
  @NotNull(message = "contractId must not be Null")
  @Column(nullable = false)
  Integer contractId;

  /** The id of the user profile. */
  @NotNull(message = "profileId must not be Null")
  @Column(nullable = false)
  Integer profileId;

  @NotBlank(message = "status must not be blank")
  @Size(min = 1, max = 10, message = "status must contain between 1 to 10 characters")
  @Column(nullable = false)
  String status;

  /** The funding amount. */
  @NotNull(message = "fundingAmount must not be null")
  @Positive(message = "fundingAmount must be positive")
  @Min(value = 1, message = "fundingAmount must be at least 1")
  @Column(nullable = false)
  Long fundingAmount;

  /** The repayment amount. */
  @NotNull(message = "repaymentAmount must not be null")
  @Positive(message = "repaymentAmount must be positive")
  @Min(value = 1, message = "repaymentAmount must be at least 1")
  @Column(nullable = false)
  Long repaymentAmount;

  /** The disbursed amount. */
  @NotNull(message = "disbursedAmount must not be null")
  @Column(nullable = false)
  Long disbursedAmount;

  /** The created timestamp. */
  @NotNull(message = "createdTimestamp must not be null")
  @Column(nullable = false)
  LocalDateTime createdTimestamp;

  @Transient UserProfile profile;

  public Funding(int contractId, int profileId, long fundingAmount, long repaymentAmount) {
    this.contractId = contractId;
    this.profileId = profileId;
    this.status = STATUS_FUNDS_IN_CONTRACT;
    this.fundingAmount = fundingAmount;
    this.repaymentAmount = repaymentAmount;
    this.disbursedAmount = 0L;
    this.createdTimestamp = LocalDateTime.now();
    this.profile = null;
  }

  public void disburse() {
    this.disbursedAmount = this.repaymentAmount;
    this.status = STATUS_FUNDS_DISBURSED;
  }
}
