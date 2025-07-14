package ai.shreds.domain.entities;

import ai.shreds.shared.dtos.SharedURLMetadataDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Domain entity representing URL metadata.
 * Contains creation timestamps, expiration information, access statistics, and organizational tags.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainURLMetadataEntity {
    
    /** Unique identifier for the metadata */
    private String metadataId;
    
    /** Reference to the parent URL entity */
    private String urlId;
    
    /** Timestamp when the URL was created */
    private Date creationDate;
    
    /** Optional expiration date for the URL */
    private Date expirationDate;
    
    /** Timestamp of the last access to the URL */
    private Date lastAccessedDate;
    
    /** Total number of times the URL has been accessed */
    private Long accessCount;
    
    /** User-defined tags for URL organization and categorization */
    private List<String> tags;
    
    /** URL to the generated QR code if available */
    private String qrCodeUrl;
    
    /** Optional user-provided description of the URL */
    private String description;
    
    /** JSON object for storing additional custom metadata */
    private Map<String, Object> customData;
    
    /**
     * Increments the access count for this URL.
     */
    public void incrementAccessCount() {
        if (this.accessCount == null) {
            this.accessCount = 0L;
        }
        this.accessCount++;
        this.lastAccessedDate = new Date();
    }
    
    /**
     * Updates the last accessed timestamp.
     */
    public void updateLastAccessed() {
        this.lastAccessedDate = new Date();
    }
    
    /**
     * Checks if the URL has expired based on the expiration date.
     * 
     * @return true if the URL is expired
     */
    public boolean isExpired() {
        if (this.expirationDate == null) {
            return false;
        }
        return new Date().after(this.expirationDate);
    }
    
    /**
     * Converts the entity to a DTO for external communication.
     * 
     * @return SharedURLMetadataDTO representation of this entity
     */
    public SharedURLMetadataDTO toDTO() {
        return SharedURLMetadataDTO.builder()
                .creationDate(this.creationDate != null ? this.creationDate.toString() : null)
                .expirationDate(this.expirationDate != null ? this.expirationDate.toString() : null)
                .accessCount(this.accessCount != null ? this.accessCount : 0L)
                .tags(this.tags != null ? this.tags.toArray(new String[0]) : new String[0])
                .build();
    }
}