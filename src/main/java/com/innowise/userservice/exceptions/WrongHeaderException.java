package com.innowise.userservice.exceptions;

public class WrongHeaderException extends RuntimeException {

    private static final String MSG = "Header must contain user id and role.";

    public WrongHeaderException() {
        super(MSG);
    }

    public WrongHeaderException(String message) {
        super(message);
    }
}
