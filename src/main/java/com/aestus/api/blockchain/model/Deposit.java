package com.aestus.api.blockchain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Deposit used for unmarshalling the Json object received from the blockchain deposit api.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Deposit extends Response {
    private String address;

    @JsonProperty("depositAmount")
    private int amount;
    private String symbol;

    @JsonProperty("units")
    private String unit;

    private String txHash;
}
