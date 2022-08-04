package com.aestus.api.transaction.model.swagger;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.transaction.model.Transaction;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with an array of Transactions as the payload. Used for schema
 * documentation in Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithTransactions extends ResponseMessage {
    @Override
    public Transaction[] getData() {
        return (Transaction[]) super.getData();
    }

    /**
     * Sets data payload.
     *
     * @param transactions the array of transactions
     */
    public void setData(Transaction[] transactions) {
        super.setData(transactions);
    }
}
