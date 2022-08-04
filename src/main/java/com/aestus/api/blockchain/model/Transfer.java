package com.aestus.api.blockchain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Transfer used for unmarshalling the Json object received from the blockchain transfer
 * api.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transfer extends Response {
  @JsonProperty("address")
  private String fromWalletId;

  @JsonProperty("receiver")
  private String toWalletId;

  @JsonProperty("transferAmount")
  private int amount;

  private String symbol;

  @JsonProperty("units")
  private String unit;

  private String txHash;
}
