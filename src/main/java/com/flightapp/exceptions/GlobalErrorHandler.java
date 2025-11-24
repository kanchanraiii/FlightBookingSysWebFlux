package com.flightapp.exceptions;

import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.flightapp.*;
import com.flightapp.model.Cities;
import com.flightapp.model.Meal;
import com.flightapp.model.TripType;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalErrorHandler {

    private static final String errorMessage = "error";

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<Map<String, String>> handleValidationErrors(WebExchangeBindException exception) {
        Map<String, String> errorMap = new HashMap<>();
        exception.getFieldErrors().forEach(err ->
                errorMap.put(err.getField(), err.getDefaultMessage())
        );
        return Mono.just(errorMap);
    }

    @ExceptionHandler(ValidationException.class)
    public Mono<Map<String, String>> handleCustomValidation(ValidationException ex) {
        return Mono.just(Map.of(errorMessage, ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return Mono.just(Map.of(errorMessage, ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Mono<Map<String, String>> handleInvalidJson(HttpMessageNotReadableException ex) {
        Map<String, String> error = new HashMap<>();
        Throwable cause = ex.getCause();

        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException invalidEx) {
            Class<?> targetType = invalidEx.getTargetType();

            if (targetType == Cities.class) {
                error.put(errorMessage, "Invalid city. Allowed values: " + Arrays.toString(Cities.values()));
                return Mono.just(error);
            }

            if (targetType == TripType.class) {
                error.put(errorMessage, "Invalid trip type. Allowed values: " + Arrays.toString(TripType.values()));
                return Mono.just(error);
            }

            if (targetType == Meal.class) {
                error.put(errorMessage, "Invalid meal type. Allowed values: " + Arrays.toString(Meal.values()));
                return Mono.just(error);
            }

            if (targetType == Boolean.class) {
                error.put(errorMessage, "Invalid boolean value. Allowed values: true or false");
                return Mono.just(error);
            }
        }

        if (cause instanceof DateTimeParseException) {
            error.put(errorMessage, "Invalid date format. Use yyyy-MM-dd");
            return Mono.just(error);
        }

        error.put(errorMessage, "Invalid JSON request");
        return Mono.just(error);
    }

    @ExceptionHandler(Exception.class)
    public Mono<Map<String, String>> handleOthers(Exception ex) {
        return Mono.just(Map.of(errorMessage, ex.getMessage()));
    }
}
