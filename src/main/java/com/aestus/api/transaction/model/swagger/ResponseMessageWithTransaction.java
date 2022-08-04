package com.aestus.api.transaction.model.swagger;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.transaction.model.Transaction;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with a Transaction as the payload. Used for schema documentation in
 * Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithTransaction extends ResponseMessage {
    @Override
    public Transaction getData() {
        return (Transaction) super.getData();
    }

    /**
     * Sets data payload.
     *
     * @param transaction the transaction
     */
    public void setData(Transaction transaction) {
        super.setData(transaction);
    }
}
