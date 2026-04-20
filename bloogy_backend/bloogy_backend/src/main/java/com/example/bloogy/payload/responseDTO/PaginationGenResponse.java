package com.example.bloogy.payload.responseDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * The `PaginationGenResponse` class is a generic data transfer object (DTO)
 * used for paginated API responses. It provides a standard structure for
 * returning a list of data and pagination-related information.
 *
 * @param <T> the type of the data contained in the paginated response
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PaginationGenResponse<T> {

    /**
     * The list of data items in the current page.
     * Represents a subset of the total data available.
     */
    private List<T> data; // Data items in the current page

    /**
     * A cursor indicating the position of the next page.
     * Used for fetching the next set of results in a paginated API.
     * If null, it indicates there are no more pages available.
     */
    private String nextCursor;
}

