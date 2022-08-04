package com.aestus.api.ledger.model;

import com.aestus.api.ledger.exception.LedgerException;
import com.aestus.api.ledger.exception.InvalidAmountException;
import com.aestus.api.ledger.exception.InvalidBalanceException;

import java.util.HashMap;

import lombok.Data;

@Data
public class Ledger {

  HashMap<String, Long> entries = new HashMap<>();

  public Ledger() {
    init();
  }

  private void init() {
    this.entries = new HashMap<>();

    this.entries.put("4zFraRa6gbst1RKjua9qT2VwEHEwH1Eqm3WJcKYuH5se", 1000000000L);
    this.entries.put("SXLRdrywXBntChoDLPjEF1KDQH95eu5EvkA4Uge1hjU", 1000000000L);
    this.entries.put("5tjN3DGqz5eYjNPipT2Vv9U2gRgqdjo62DE1QjxbC5DK", 1000000000L);
    this.entries.put("6FVgAaLL8avbKAVWLB64sZJ3j8zJToiuNZFbvpHBrzFc", 1000000000L);
    this.entries.put("qJJZvUgCRtJMNHqq91EcoStYw8NhWyszrtWRVLhVmw4", 1000000000L);
    this.entries.put("CbgZoPiQnnASxoYnerEAumW67EJrVkcHcCNeLCQbdBNA", 1000000000L);
    this.entries.put("GAyCywe7wYQ49XA92BrDBVvj2CMKeEGMmGjseQR3yFua", 0L);
  }

  public void transfer(String fromWalletId, String toWalletId, long amount) throws LedgerException {

    if (amount <= 0) throw new InvalidAmountException(amount);

    Long fromBalance = this.entries.get(fromWalletId);
    Long toBalance = this.entries.get(toWalletId);

    if (fromBalance == null) throw new InvalidBalanceException(fromWalletId);
    if (toBalance == null) throw new InvalidBalanceException(toWalletId);

    fromBalance -= amount;
    toBalance += amount;

    this.entries.put(fromWalletId, fromBalance);
    this.entries.put(toWalletId, toBalance);
  }

  public Long getBalance(String walletId) {
    return this.entries.get(walletId);
  }

  public HashMap<String, Long> getEntries() {
    return this.entries;
  }
}
