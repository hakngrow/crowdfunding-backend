package com.aestus.api.contract.exception;

import com.aestus.api.request.exception.RequestException;

public class GetProposalException extends RequestException {
  public GetProposalException(String message) {
    super(message);
  }
}
