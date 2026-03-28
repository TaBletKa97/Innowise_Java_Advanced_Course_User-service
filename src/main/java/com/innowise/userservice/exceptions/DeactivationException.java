package com.innowise.userservice.exceptions;

public class DeactivationException extends RuntimeException {
    public static final String DEACTIVATION_ERROR_MESSAGE = "Target instance is already not active";

    public DeactivationException() {
        super(DEACTIVATION_ERROR_MESSAGE);
    }

    public DeactivationException(String message) {
        super(message);
    }

    public DeactivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
