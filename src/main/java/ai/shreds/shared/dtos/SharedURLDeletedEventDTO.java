package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event DTO for URL deletions.
 * Contains information about URL deletion events.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedURLDeletedEventDTO {
    /** Identifier of the deleted URL. */
    private String urlId;

    /** Short code of the deleted URL. */
    private String shortCode;

    /** Type of deletion (e.g., soft, hard). */
    private String deletionType;

    /** ISO-8601 timestamp when deletion occurred. */
    private String deletionTimestamp;

    /** Owner (user) who performed the deletion. */
    private String owner;
}