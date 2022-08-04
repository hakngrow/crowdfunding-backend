package com.aestus.api.contract.exception;

public class RequestIdNotFoundException extends ContractException {
  public RequestIdNotFoundException(int requestId) {
    super(String.format("Contract with requestId=%s not found", requestId));
  }
}
