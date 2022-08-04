package com.aestus.api.request.exception;

public class InvalidRequestTypeException extends RequestException {
    public InvalidRequestTypeException(String type) {
        super(String.format("Unable to process request of type=%s", type));
    }
}
