package com.aestus.api.product.model.swagger;

import com.aestus.api.common.model.ResponseMessage;
import com.aestus.api.product.model.Product;

public class ResponseMessageWithProduct extends ResponseMessage {

    @Override
    public Product getData() {
        return (Product) super.getData();
    }

    /**
     * Sets data payload.
     *
     * @param product the product
     */
    public void setData(Product product) {
        super.setData(product);
    }
}
