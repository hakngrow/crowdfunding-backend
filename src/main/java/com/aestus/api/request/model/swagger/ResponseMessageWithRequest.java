package com.aestus.api.request.model.swagger;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.request.model.Request;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with a Request as the payload. Used for schema documentation in
 * Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithRequest extends ResponseMessage {
    @Override
    public Request getData() {
        return (Request) super.getData();
    }

    /**
     * Sets data payload.
     *
     * @param request the request payload
     */
    public void setData(Request request) {
        super.setData(request);
    }
}
