package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO carrying metadata information for a shortened URL.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedURLMetadataDTO {
    /** ISO-8601 creation timestamp. */
    private String creationDate;

    /** ISO-8601 expiration timestamp, if any. */
    private String expirationDate;

    /** Total number of times the URL was accessed. */
    private Long accessCount;

    /** User-defined tags associated with the URL. */
    private String[] tags;

    // Manual getters and setters to fix compilation issues
    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Long getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(Long accessCount) {
        this.accessCount = accessCount;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}
