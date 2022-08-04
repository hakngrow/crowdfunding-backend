package com.aestus.api.contract.exception;

public class ContractAmountsException extends ContractException {
  public ContractAmountsException(long targetAmount, long repaymentAmount) {
    super(
        String.format(
            "Contract repayment amount=%d, must be more than target amount=%d",
            repaymentAmount, targetAmount));
  }
}
