package com.aestus.api.request.model.swagger;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.request.model.RequestForProposal;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with an array of Requests as the payload. Used for schema
 * documentation in Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithRequestForProposals extends ResponseMessage {
  @Override
  public RequestForProposal[] getData() {
    return (RequestForProposal[]) super.getData();
  }

  /**
   * Sets data payload.
   *
   * @param rfps the array of request for proposals
   */
  public void setData(RequestForProposal[] rfps) {
    super.setData(rfps);
  }
}
