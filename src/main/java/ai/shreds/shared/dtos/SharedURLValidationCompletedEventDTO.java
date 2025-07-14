package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event DTO representing the completion of URL validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedURLValidationCompletedEventDTO {
    /** ID of URL validated */
    private String urlId;

    /** Result details of validation */
    private SharedValidationResultDTO validationResult;

    /** ISO-8601 timestamp of validation completion */
    private String timestamp;
}