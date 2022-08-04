package com.aestus.api.blockchain.model.swagger;

import com.aestus.api.blockchain.model.Balance;
import com.aestus.api.common.model.ResponseMessage;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with a Balance instance as the payload. Used for schema documentation in
 * Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithBalance extends ResponseMessage {
    @Override
    public Balance getData() {
        return (Balance) super.getData();
    }

    /**
     * Sets data payload.
     *
     * @param balance the wallet balance
     */
    public void setData(Balance balance) {
        super.setData(balance);
    }
}
