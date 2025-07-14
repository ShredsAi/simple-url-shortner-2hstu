package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for capturing errors during bulk URL operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedBulkOperationErrorDTO {
    /** The URL involved in the error. */
    private String url;

    /** Description of the error. */
    private String error;
}