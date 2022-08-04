package com.aestus.api.contract.model.swagger;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.contract.model.Contract;

public class ResponseMessageWithContract extends ResponseMessage {
  @Override
  public Contract getData() {
    return (Contract) super.getData();
  }

  /**
   * Sets data payload.
   *
   * @param contract the contract
   */
  public void setData(Contract contract) {
    super.setData(contract);
  }
}
