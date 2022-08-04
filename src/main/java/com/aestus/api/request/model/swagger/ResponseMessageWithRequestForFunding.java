package com.aestus.api.request.model.swagger;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.request.model.RequestForFunding;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with a RequestForFunding as the payload. Used for schema
 * documentation in Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithRequestForFunding extends ResponseMessage {
  @Override
  public RequestForFunding getData() {
    return (RequestForFunding) super.getData();
  }

  /**
   * Sets data payload.
   *
   * @param rff the request for funding payload
   */
  public void setData(RequestForFunding rff) {
    super.setData(rff);
  }
}
