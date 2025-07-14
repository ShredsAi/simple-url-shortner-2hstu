package ai.shreds.shared.dtos;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO representing the response of a bulk URL operation.
 * Contains information about the results of processing multiple URLs in a batch.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedBulkOperationResponseDTO {

    /** Total number of URLs requested in the bulk operation */
    private Integer totalRequested;

    /** Number of URLs successfully processed */
    private Integer successful;

    /** Number of URLs that failed during processing */
    private Integer failed;

    /** Array of successful URL responses */
    private SharedURLResponseDTO[] results;

    /** Array of errors for failed URL operations */
    private SharedBulkOperationErrorDTO[] errors;
}