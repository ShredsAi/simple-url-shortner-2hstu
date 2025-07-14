package ai.shreds.domain.entities;

import ai.shreds.shared.value_objects.SharedEnumURLStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Domain entity representing URL mapping for fast short code resolution.
 * Contains the mapping between short codes and original URLs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainURLMappingEntity {
    
    /** Unique identifier for the mapping */
    private String mappingId;
    
    /** The short code or custom alias used as the key for URL resolution */
    private String shortCode;
    
    /** The target URL that the short code redirects to */
    private String originalUrl;
    
    /** Reference to the parent URL entity */
    private String urlId;
    
    /** Current status of the mapping */
    private SharedEnumURLStatus status;
    
    /** Timestamp when the mapping was created */
    private Date createdAt;
    
    /**
     * Checks if the mapping is currently active.
     * 
     * @return true if the mapping status is ACTIVE
     */
    public boolean isActive() {
        return SharedEnumURLStatus.ACTIVE.equals(this.status);
    }
    
    /**
     * Deactivates the mapping by setting its status to DEACTIVATED.
     */
    public void deactivate() {
        this.status = SharedEnumURLStatus.DEACTIVATED;
    }
    
    /**
     * Marks the mapping as expired by setting its status to EXPIRED.
     */
    public void markExpired() {
        this.status = SharedEnumURLStatus.EXPIRED;
    }
    
    /**
     * Marks the mapping as deleted by setting its status to DELETED.
     */
    public void markDeleted() {
        this.status = SharedEnumURLStatus.DELETED;
    }
}