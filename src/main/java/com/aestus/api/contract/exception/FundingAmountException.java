package com.aestus.api.contract.exception;

public class FundingAmountException extends ContractException {
  public FundingAmountException(long fundingAmount, long outstandingAmount) {
    super(
        String.format(
            "Funding amount of %d, exceeds outstanding amount of %d",
            fundingAmount, outstandingAmount));
  }
}
