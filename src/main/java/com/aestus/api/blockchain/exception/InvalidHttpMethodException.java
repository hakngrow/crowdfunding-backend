package com.aestus.api.blockchain.exception;

import org.springframework.http.HttpMethod;

public class InvalidHttpMethodException extends BlockchainException {
    public InvalidHttpMethodException(HttpMethod method) {
        super("Invalid Http method: " + method.name());
    }
}
