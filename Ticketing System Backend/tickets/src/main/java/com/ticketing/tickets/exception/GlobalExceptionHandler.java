package com.ticketing.tickets.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Handle ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        log.error("ResourceNotFoundException: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<?> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex, WebRequest request) {
        log.error("ResourceAlreadyExistsException: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Handle global exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Exception: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", "Internal server error");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle validation errors
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        log.error("MethodArgumentNotValidException: {}", ex.getMessage());        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
