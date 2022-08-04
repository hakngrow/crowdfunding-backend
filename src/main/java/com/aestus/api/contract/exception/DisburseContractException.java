package com.aestus.api.contract.exception;

public class DisburseContractException extends ContractException {
  public DisburseContractException(String currentStatus) {
    super(
        String.format(
            "Funds can only be disbursed when in Repaid (RP) status, current contract status=%s",
            currentStatus));
  }
}
