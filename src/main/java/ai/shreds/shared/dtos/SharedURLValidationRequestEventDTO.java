package ai.shreds.shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for requesting URL validation from the validation service.
 * Contains information needed to validate a URL.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedURLValidationRequestEventDTO {
    /** The ID of the URL to validate. */
    private String urlId;

    /** The original URL string to be validated. */
    private String originalUrl;

    /** Priority of the validation (e.g., LOW, MEDIUM, HIGH). */
    private String validationPriority;

    /** ISO-8601 timestamp when the validation request was made. */
    private String requestTimestamp;
}