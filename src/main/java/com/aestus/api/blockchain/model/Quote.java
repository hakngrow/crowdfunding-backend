package com.aestus.api.blockchain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Quote used for unmarshalling the Json object received from the blockchain exchange rate
 * api.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Quote {
  @JsonProperty("time")
  private LocalDateTime timestamp;

  @JsonProperty("asset_id_base")
  private String baseAssetId;

  @JsonProperty("asset_id_quote")
  private String quoteAssetId;

  @JsonProperty("rate")
  private double rate;
}
