package com.aestus.api.product.model.swagger;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.product.model.Product;

public class ResponseMessageWithProducts extends ResponseMessage {

    @Override
    public Product[] getData() {
        return (Product[]) super.getData();
    }

    /**
     * Sets data payload.
     *
     * @param products the array of product
     */
    public void setData(Product[] products) {
        super.setData(products);
    }
}
