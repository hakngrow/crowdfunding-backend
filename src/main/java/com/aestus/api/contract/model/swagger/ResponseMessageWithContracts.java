package com.aestus.api.contract.model.swagger;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.contract.model.Contract;

public class ResponseMessageWithContracts extends ResponseMessage {

    @Override
    public Contract[] getData() {
        return (Contract[]) super.getData();
    }

    /**
     * Sets data payload.
     *
     * @param contracts the array of contract
     */
    public void setData(Contract[] contracts) {
        super.setData(contracts);
    }
}
