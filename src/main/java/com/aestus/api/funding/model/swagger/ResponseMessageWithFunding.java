package com.aestus.api.funding.model.swagger;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.funding.model.Funding;

public class ResponseMessageWithFunding extends ResponseMessage {
  @Override
  public Funding getData() {
    return (Funding) super.getData();
  }

  /**
   * Sets data payload.
   *
   * @param funding the funding
   */
  public void setData(Funding funding) {
    super.setData(funding);
  }
}
