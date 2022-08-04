package com.aestus.api.request.model.swagger;

import com.aestus.api.common.model.ResponseMessage;

import com.aestus.api.request.model.Request;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with an array of Requests as the payload. Used for schema
 * documentation in Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithRequests extends ResponseMessage {
  @Override
  public Request[] getData() {
    return (Request[]) super.getData();
  }

  /**
   * Sets data payload.
   *
   * @param requests the array of requests
   */
  public void setData(Request[] requests) {
    super.setData(requests);
  }
}
