package com.aestus.api.blockchain.model.swagger;

import com.aestus.api.blockchain.model.Deposit;
import com.aestus.api.common.model.ResponseMessage;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with a Deposit instance as the payload. Used for schema documentation in
 * Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithDeposit extends ResponseMessage {
    @Override
    public Deposit getData() {
        return (Deposit) super.getData();
    }

    /**
     * Sets data payload.
     *
     * @param deposit the deposit transaction receipt
     */
    public void setData(Deposit deposit) {
        super.setData(deposit);
    }
}
