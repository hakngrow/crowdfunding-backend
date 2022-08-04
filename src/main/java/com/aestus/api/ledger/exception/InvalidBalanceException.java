package com.aestus.api.ledger.exception;

public class InvalidBalanceException extends LedgerException {
    public InvalidBalanceException(String walletId) {
        super(String.format("Invalid balance @ wallet address %s", walletId));
    }
}
