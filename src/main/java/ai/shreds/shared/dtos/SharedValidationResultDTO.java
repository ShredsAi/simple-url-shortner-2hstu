package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the result of URL validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedValidationResultDTO {
    /** Whether the URL passed validation. */
    private Boolean isValid;

    /** Security score of the URL (0-100). */
    private Integer securityScore;

    /** Detected threats, if any. */
    private String[] threats;

    /** Content categories of the URL. */
    private String[] categories;
}