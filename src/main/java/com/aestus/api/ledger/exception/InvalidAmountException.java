package com.aestus.api.ledger.exception;

public class InvalidAmountException extends LedgerException {
  public InvalidAmountException(long amount) {
    super(String.format("Amount must be positive: %d", amount));
  }
}
