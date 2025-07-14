package ai.shreds.shared.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for updating a shortened URL's expiration date and tags.
 * Contains fields that can be modified after URL creation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedUpdateURLRequestDTO {

    /**
     * New expiration date in ISO-8601 format (optional)
     * If provided, updates the URL's expiration date
     */
    private String expirationDate;

    /**
     * Updated tags for the URL (optional)
     * Replaces the existing tags array with the new one
     */
    private String[] tags;

    // Manual getters and setters to fix compilation issues
    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}
