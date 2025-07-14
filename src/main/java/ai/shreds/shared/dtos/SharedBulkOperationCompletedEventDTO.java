package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event DTO published when a bulk operation completes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedBulkOperationCompletedEventDTO {

    /** Unique identifier of the bulk operation. */
    private String operationId;

    /** Type of the bulk operation (CREATE, UPDATE, DELETE). */
    private String operationType;

    /** Total number of URLs requested in the operation. */
    private Integer totalRequested;

    /** Number of successful operations. */
    private Integer successful;

    /** Number of failed operations. */
    private Integer failed;

    /** Owner (user) who initiated the operation. */
    private String owner;

    /** ISO-8601 timestamp when the operation completed. */
    private String completionTimestamp;
}