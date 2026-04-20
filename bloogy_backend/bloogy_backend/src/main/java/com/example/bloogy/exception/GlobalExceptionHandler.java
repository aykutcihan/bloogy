package com.example.bloogy.exception;

import com.example.bloogy.payload.responseDTO.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles cases where an entity is not found in the database.
     *
     * Example:
     * - Trying to fetch an article with an invalid ID.
     *
     * Returns:
     * - HTTP 404 (Not Found) with a custom error message.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public GenericResponse<?> handleEntityNotFound(EntityNotFoundException ex) {
        return GenericResponse.builder()
                .message(ex.getMessage())
                .httpStatus(HttpStatus.NOT_FOUND)
                .data(null)
                .build();
    }

    /**
     * Handles business logic validation exceptions.
     *
     * Example:
     * - Violating a business rule, such as trying to delete a protected resource.
     *
     * Returns:
     * - HTTP 400 (Bad Request) with a detailed error message.
     */
    @ExceptionHandler(BusinessValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public GenericResponse<?> handleValidationException(BusinessValidationException ex) {
        return GenericResponse.builder()
                .message(ex.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .data(null)
                .build();
    }

    /**
     * Handles database-related exceptions.
     *
     * Example:
     * - Issues like constraint violations, or database unavailability.
     *
     * Returns:
     * - HTTP 500 (Internal Server Error) with a detailed error message.
     */
    @ExceptionHandler(DatabaseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse<?> handleDatabaseException(DatabaseException ex) {
        return GenericResponse.builder()
                .message(ex.getMessage())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .data(null)
                .build();
    }

    /**
     * Handles all other unexpected exceptions in the application.
     *
     * Returns:
     * - HTTP 500 (Internal Server Error) with a generic error message.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse<?> handleGenericException(Exception ex) {
        return GenericResponse.builder()
                .message("An unexpected error occurred.")
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .data(null)
                .build();
    }

    /**
     * Handles validation errors thrown by @Valid annotations on request objects.
     *
     * Captures all field-specific validation messages and formats them
     * into a key-value pair (field name and error message).
     *
     * Example:
     * - A request body with missing or invalid fields.
     *
     * Returns:
     * - HTTP 400 (Bad Request) with a map of field-specific error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleRequestValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
