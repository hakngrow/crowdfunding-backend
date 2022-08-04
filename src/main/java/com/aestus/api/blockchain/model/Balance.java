package com.aestus.api.blockchain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Balance used for unmarshalling the Json object received from the blockchain get-balance
 * api.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Balance {

  /** The wallet address. */
  String address;

  /** The wallet balance. */
  Long balance;

  /** The symbol. */
  String symbol;

  /** The Unit. */
  String unit;
}
