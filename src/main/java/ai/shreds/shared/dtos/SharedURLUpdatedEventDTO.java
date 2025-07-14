package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event DTO for URL updates.
 * Contains information about updates made to a shortened URL.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedURLUpdatedEventDTO {
    /** Identifier of the updated URL. */
    private String urlId;

    /** Short code of the URL. */
    private String shortCode;

    /** Fields that were updated. */
    private String[] updatedFields;

    /** ISO-8601 timestamp of the update event. */
    private String updateTimestamp;

    /** Owner (user) who performed the update. */
    private String owner;
}