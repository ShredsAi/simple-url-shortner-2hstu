package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for events published when a new URL is created.
 * Contains all relevant information about the newly created URL.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedURLCreatedEventDTO {
    /** Unique identifier of the created URL. */
    private String urlId;

    /** Generated short code for the URL. */
    private String shortCode;

    /** Original long URL. */
    private String originalUrl;

    /** Owner (user) who created the URL. */
    private String owner;

    /** ISO-8601 timestamp of creation. */
    private String creationTimestamp;

    /** Associated metadata of the URL. */
    private SharedURLMetadataDTO metadata;
}