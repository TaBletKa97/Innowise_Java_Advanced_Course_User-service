package com.innowise.userservice.controller.exceptionhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ExceptionsUtils {

    private ExceptionsUtils() {
    }

    public static void logError(Class<?> currentClass , Throwable e) {
        Logger log = LoggerFactory.getLogger(currentClass);

        String stacktrace = Arrays.stream(e.getStackTrace())
                .map(String::valueOf)
                .collect(Collectors.joining("\n"));
        log.error("{}\n{}\n{}\n", e.getClass(), e.getMessage(), stacktrace);
    }
}
