package com.aestus.api.blockchain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Response used for unmarshalling the response Json object received from the blockchain
 * api.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

  /** The returned http status code. */
  protected int code;

  /** The error message if any. */
  protected String message;

  /**
   * Checks if there are any error messages
   *
   * @return {@code true} if {@code message} equals {@code null}
   */
  public boolean isOk() {
    return this.message == null;
  }
}
