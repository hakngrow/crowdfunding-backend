package com.aestus.api.request.exception;

public class InvalidRequestIdException extends RequestException {
    public InvalidRequestIdException(Integer id) {
        super("Unable to process request with requestId="+ id);
    }
}
