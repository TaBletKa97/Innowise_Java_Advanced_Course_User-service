package com.innowise.userservice.controller;

import com.innowise.userservice.repository.exceptions.ActivationException;
import com.innowise.userservice.repository.exceptions.DeactivationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.NoSuchElementException;

@ControllerAdvice
public class CommonExceptionHandler {

    private static final String VALIDATION_PARAMS = """
            Id should be positive.
            Name or surname should contain from 2 to 50 characters.
            Email should be real.
            Card number should be real.""";
    private static final Logger log = LoggerFactory.getLogger(CommonExceptionHandler.class);

    @ExceptionHandler
    public ResponseEntity<?> handlingGeneralException(Throwable e) {
        log.error("{}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(exception = {ActivationException.class, DeactivationException.class,
            IllegalArgumentException.class})
    public ResponseEntity<?> handlingIllegalArgumentException(Throwable e) {

        log.error("{}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    public ResponseEntity<?> handlingValidationException(Throwable e) {

        log.error("{}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(VALIDATION_PARAMS);
    }

    @ExceptionHandler(exception = NoSuchElementException.class)
    public ResponseEntity<?> handlingNoElementException(Throwable e) {

        log.error("{}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}