package com.innowise.userservice.exceptions;

public class CardLimitViolationException extends RuntimeException {

    public static final String CARD_LIMIT_MSG = "User can have only 5 cards or less";

    public CardLimitViolationException() {
        super(CARD_LIMIT_MSG);
    }

    public CardLimitViolationException(String message) {
        super(message);
    }

    public CardLimitViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
