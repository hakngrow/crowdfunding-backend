package com.aestus.api.request.model.swagger;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.request.model.Proposal;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with a Proposal as the payload. Used for schema documentation in
 * Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithProposal extends ResponseMessage {
    @Override
    public Proposal getData() {
        return (Proposal) super.getData();
    }

    /**
     * Sets data payload.
     *
     * @param proposal the proposal payload
     */
    public void setData(Proposal proposal) {
        super.setData(proposal);
    }
}
