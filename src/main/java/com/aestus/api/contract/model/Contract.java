package com.aestus.api.contract.model;

import com.aestus.api.contract.exception.FundingAmountException;
import com.aestus.api.funding.model.Funding;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.aestus.api.request.model.Request;
import com.aestus.api.request.model.RequestForFunding;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** The type Contract. */
@Slf4j
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contracts")
public class Contract {

  /** The constant STATUS_NF. */
  public static final String STATUS_NOT_FUNDED = "NF"; // Not funded

  /** The constant STATUS_PF. */
  public static final String STATUS_PARTIALLY_FUNDED = "PF"; // Partially funded

  /** The constant STATUS_FF. */
  public static final String STATUS_FULLY_FUNDED = "FF"; // Fully funded

  /** The constant STATUS_FTP. */
  public static final String STATUS_FUNDS_TRANSFERRED_TO_PROVIDER = "FTP"; // Funds transferred to solution provider

  public static final String STATUS_FUNDS_REPAID = "RP"; // Funds repaid by user
  public static final String STATUS_FUNDS_DISBURSED = "FD"; // Funds disbursed to investors

  /** The Id. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  /** The id of the request for funding. */
  @NotNull(message = "requestId must not be Null")
  @Column(nullable = false, unique = true)
  Integer requestId;

  /** The wallet id of the contract. */
  @Size(min = 10, max = 100, message = "walletId must contain between 10 to 100 characters")
  @Pattern(regexp = "[a-zA-Z0-9]+", message = "walletId must not contain special characters")
  @NotNull(message = "walletId must not be Null")
  @Column(nullable = false)
  String walletId;

  /** The target funding amount. */
  @NotNull(message = "targetAmount must not be null")
  @Positive(message = "targetAmount must be positive")
  @Min(value = 1, message = "targetAmount must be at least 1")
  @Column(nullable = false)
  Long targetAmount;

  /** The repayment amount. */
  @NotNull(message = "repaymentAmount must not be null")
  @Positive(message = "repaymentAmount must be positive")
  @Min(value = 1, message = "repaymentAmount must be at least 1")
  @Column(nullable = false)
  Long repaymentAmount;

  /** The Status. */
  @NotBlank(message = "status must not be blank")
  @Size(min = 1, max = 10, message = "status must contain between 1 to 10 characters")
  @Column(nullable = false)
  String status;

  /** The created timestamp. */
  @NotNull(message = "createdTimestamp must not be null")
  @Column(nullable = false)
  LocalDateTime createdTimestamp;

  /** The Fundings. */
  @Transient List<Funding> fundings = new ArrayList<>();

  @Transient RequestForFunding request;

  /**
   * Instantiates a new Contract.
   *
   * @param id the id
   * @param requestId the request id
   * @param walletId the wallet id
   * @param targetAmount the target amount
   * @param repaymentAmount the repayment amount
   * @param createdTimestamp the created timestamp
   */
  public Contract(
      Integer id,
      Integer requestId,
      String walletId,
      Long targetAmount,
      Long repaymentAmount,
      LocalDateTime createdTimestamp) {
    this.id = id;
    this.requestId = requestId;
    this.walletId = walletId;
    this.targetAmount = targetAmount;
    this.repaymentAmount = repaymentAmount;
    this.status = STATUS_NOT_FUNDED;
    this.createdTimestamp = createdTimestamp;
    this.fundings = new ArrayList<Funding>();
  }

  // Instantiating a Contract instance from a Request instance
  public Contract(Request requestForFunding) {
    this(
        Integer.valueOf(0),
        requestForFunding.getId(),
        "GAyCywe7wYQ49XA92BrDBVvj2CMKeEGMmGjseQR3yFua",
        requestForFunding.getCost(),
        requestForFunding.getRepayment(),
        LocalDateTime.now());
  }

  /**
   * Gets the contract yield.
   *
   * @return the yield
   */
  public Long getYield() {
    BigDecimal decimal =
        BigDecimal.valueOf(repaymentAmount.doubleValue() / targetAmount * 100 - 100);
    log.info(decimal.toString());
    log.info(String.valueOf(this.repaymentAmount.doubleValue() / targetAmount));
    return decimal.longValue();
  }

  /**
   * Gets the contract raised amount from all fundings.
   *
   * @return the raised amount
   */
  public Long getRaisedAmount() {
    if (fundings == null || fundings.isEmpty()) return 0L;
    else
      return fundings.stream()
          .reduce(0L, (subtotal, funding) -> subtotal + funding.getFundingAmount(), Long::sum);
  }

  /**
   * Gets the contract outstanding amount from target.
   *
   * @return the outstanding amount
   */
  public long getOutstandingAmount() {
    return targetAmount - getRaisedAmount();
  }

  /**
   * Gets the contract funding percentage.
   *
   * @param contract the contract
   * @param amount the amount of funding
   * @return the funding percentage
   */
  public static int getFundingPercentage(Contract contract, long amount) {
    return BigDecimal.valueOf(amount / contract.getTargetAmount().doubleValue() * 100).intValue();
  }

  /**
   * Gets the contract funding returns.
   *
   * @param amount the amount of funding
   * @return the funding returns
   */
  public Long getFundingReturns(long amount) {
    BigDecimal decimal = BigDecimal.valueOf(amount * (getYield() + 100) / 100D);

    log.info(decimal.toString());
    log.info(String.valueOf(decimal.longValue()));

    return decimal.longValue();
  }

  /**
   * Fund the contract.
   *
   * @param profileId the profile id
   * @param fundingAmount the funding amount
   * @return the funding instance
   * @throws FundingAmountException the funding amount exception
   */
  public Funding fund(int profileId, long fundingAmount) throws FundingAmountException {

    long outstandingAmt = getOutstandingAmount();
    if (fundingAmount <= outstandingAmt) {

      Funding funding = new Funding(id, profileId, fundingAmount, getFundingReturns(fundingAmount));

      // Seems like the returned list is immutable and results in UnsupportedOperationException
      // Need to create a new list (based on the old) to get around the problem
      ArrayList fundings = new ArrayList<Funding>(getFundings());
      fundings.add(funding);

      setFundings(fundings);

      if (fundingAmount == outstandingAmt) setStatus(STATUS_FULLY_FUNDED);
      else setStatus(STATUS_PARTIALLY_FUNDED);

      return funding;

    } else throw new FundingAmountException(fundingAmount, outstandingAmt);
  }

  public void disburse() {

    if (this.status.equals(STATUS_FUNDS_REPAID)) {

      this.status = STATUS_FUNDS_DISBURSED;

      for (Funding funding : this.fundings) funding.disburse();
    }
  }
}
