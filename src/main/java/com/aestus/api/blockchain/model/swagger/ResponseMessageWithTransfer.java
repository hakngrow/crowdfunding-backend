package com.aestus.api.blockchain.model.swagger;

import com.aestus.api.blockchain.model.Transfer;
import com.aestus.api.common.model.ResponseMessage;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An extended ResponseMessage with a Transfer instance as the payload. Used for schema documentation
 * in Swagger.
 */
@Data
@AllArgsConstructor
public class ResponseMessageWithTransfer extends ResponseMessage {
    @Override
    public Transfer getData() {
        return (Transfer) super.getData();
    }

    /**
     * Sets data payload.
     *
     * @param transfer the transfer transaction receipt
     */
    public void setData(Transfer transfer) {
        super.setData(transfer);
    }
}
