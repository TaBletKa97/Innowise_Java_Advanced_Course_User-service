package com.innowise.userservice.controller.exceptionhandlers;

import com.innowise.userservice.service.exceptions.ActivationException;
import com.innowise.userservice.service.exceptions.CardLimitViolationException;
import com.innowise.userservice.service.exceptions.DeactivationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

import static com.innowise.userservice.controller.exceptionhandlers.ExceptionsUtils.logError;

@ControllerAdvice
public class CommonExceptionHandler {

    private static final String VALIDATION_PARAMS = """
            Id should be positive.
            Name or surname should contain from 2 to 50 characters.
            Email should be real.
            Card number should be real.""";


    @ExceptionHandler
    public ResponseEntity<String> handlingGeneralException(Throwable e) {
        logError(this.getClass(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(exception = {ActivationException.class, DeactivationException.class,
            IllegalArgumentException.class, CardLimitViolationException.class,
            UnsupportedOperationException.class})
    public ResponseEntity<String> handlingIllegalArgumentException(Throwable e) {
        logError(this.getClass(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    public ResponseEntity<String> handlingValidationException(Throwable e) {
        logError(this.getClass(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(VALIDATION_PARAMS);
    }

    @ExceptionHandler(exception = NoSuchElementException.class)
    public ResponseEntity<String> handlingNoElementException(Throwable e) {
        logError(this.getClass(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

}