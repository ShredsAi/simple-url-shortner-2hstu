package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic DTO for API error responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedErrorResponseDTO {
    /** ISO-8601 timestamp when the error occurred. */
    private String timestamp;

    /** HTTP status code of the error. */
    private Integer status;

    /** Short error phrase (e.g., "Bad Request"). */
    private String error;

    /** Detailed error message. */
    private String message;

    /** The request path that caused the error. */
    private String path;
}