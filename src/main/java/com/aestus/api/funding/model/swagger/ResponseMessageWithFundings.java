package com.aestus.api.funding.model.swagger;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.funding.model.Funding;

public class ResponseMessageWithFundings extends ResponseMessage {

  @Override
  public Funding[] getData() {
    return (Funding[]) super.getData();
  }

  /**
   * Sets data payload.
   *
   * @param fundings the array of funding
   */
  public void setData(Funding[] fundings) {
    super.setData(fundings);
  }
}
