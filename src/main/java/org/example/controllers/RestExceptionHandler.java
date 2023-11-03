package org.example.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.example.validations.ValidationErrorResponse;
import org.example.validations.ValidationException;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler {
    private final String TRACE_ID_KEY = "X-Trace-Id";

    private String getTraceId() {
        return MDC.get(TRACE_ID_KEY) == null ? "" : MDC.get(TRACE_ID_KEY);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        final List<String> errors = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new ValidationErrorResponse(getTraceId(), errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ValidationErrorResponse> handleMissingParametersErrors(ConstraintViolationException ex) {
        final List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new ValidationErrorResponse(getTraceId(), errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<ValidationErrorResponse> handleNonExistingLanguage(ValidationException ex) {
        final List<String> errors = List.copyOf(ex.getErrors());

        new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new ValidationErrorResponse(getTraceId(), errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
