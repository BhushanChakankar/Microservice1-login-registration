package com.extwebtech.registration.exception;
public class RequiredFieldMissingException extends RuntimeException {

    public RequiredFieldMissingException(String message) {
        super(message);
    }
}