package com.example.bloogy.payload.responseDTO;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * Represents a generic response structure for API responses.
 *
 * @param <T> The type of the data included in the response.
 */
@Data
@Builder
public class GenericResponse<T> {

    /**
     * A message providing details about the response (e.g., success or error messages).
     */
    private String message;

    /**
     * The HTTP status code associated with the response.
     */
    private HttpStatus httpStatus;

    /**
     * The data payload included in the response.
     * This can be of any type, depending on the API endpoint.
     */
    private T data;
}
