package com.aestus.api.request.exception;

public class InvalidRequestStatusException extends RequestException {
    public InvalidRequestStatusException(String status) {
        super(String.format("Unable to process request of status=%s", status));
    }
}
