package com.aestus.api.request.model.swagger;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.request.model.RequestForFunding;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with an array of Requests as the payload. Used for schema
 * documentation in Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithRequestForFundings extends ResponseMessage {
    @Override
    public RequestForFunding[] getData() {
        return (RequestForFunding[]) super.getData();
    }

    /**
     * Sets data payload.
     *
     * @param rffs the array of request for fundings
     */
    public void setData(RequestForFunding[] rffs) {
        super.setData(rffs);
    }
}
