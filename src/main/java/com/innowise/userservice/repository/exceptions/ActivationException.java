package com.innowise.userservice.repository.exceptions;

public class ActivationException extends RuntimeException {
    public static final String ACTIVATION_ERROR_MESSAGE = "Target instance is already active";

    public ActivationException() {
        super(ACTIVATION_ERROR_MESSAGE);
    }

    public ActivationException(String message) {
        super(message);
    }

    public ActivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
