package com.aestus.api.request.model.swagger;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.request.model.RequestForProposal;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with a RequestForProposal as the payload. Used for schema
 * documentation in Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithRequestForProposal extends ResponseMessage {
  @Override
  public RequestForProposal getData() {
    return (RequestForProposal) super.getData();
  }

  /**
   * Sets data payload.
   *
   * @param rfp the request for proposal payload
   */
  public void setData(RequestForProposal rfp) {
    super.setData(rfp);
  }
}
