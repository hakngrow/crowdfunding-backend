package com.aestus.api.request.model.swagger;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.request.model.Proposal;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with an array of Proposals as the payload. Used for schema
 * documentation in Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithProposals extends ResponseMessage {
    @Override
    public Proposal[] getData() {
        return (Proposal[]) super.getData();
    }

    /**
     * Sets data payload.
     *
     * @param proposals the array of proposals
     */
    public void setData(Proposal[] proposals) {
        super.setData(proposals);
    }
}
