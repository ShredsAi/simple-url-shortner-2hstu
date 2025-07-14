package ai.shreds.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Domain entity representing a custom alias for a shortened URL.
 * Contains reservation and assignment logic for custom aliases.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainCustomAliasEntity {
    
    /** Unique identifier for the alias */
    private String aliasId;
    
    /** The custom alias value */
    private String aliasValue;
    
    /** Whether the alias is reserved */
    private Boolean isReserved;
    
    /** Owner of the alias */
    private String owner;
    
    /** URL ID this alias is assigned to */
    private String urlId;
    
    /** Timestamp when the alias was created */
    private Date createdAt;
    
    /** Timestamp when the alias expires */
    private Date expiresAt;
    
    /**
     * Checks if the alias is available for use.
     * 
     * @return true if the alias is available
     */
    public boolean isAvailable() {
        boolean notExpired = expiresAt == null || new Date().before(expiresAt);
        return !Boolean.TRUE.equals(isReserved) && urlId == null && notExpired;
    }
    
    /**
     * Reserves the alias for the specified owner.
     * 
     * @param owner The owner to reserve the alias for
     */
    public void reserve(String owner) {
        if (Boolean.TRUE.equals(this.isReserved)) {
            throw new IllegalStateException("Alias is already reserved");
        }
        this.isReserved = true;
        this.owner = owner;
    }
    
    /**
     * Releases the alias making it available for use.
     */
    public void release() {
        this.isReserved = false;
        this.owner = null;
        this.urlId = null;
        this.expiresAt = null;
    }
    
    /**
     * Assigns the alias to a specific URL.
     * 
     * @param urlId The URL ID to assign the alias to
     */
    public void assignToURL(String urlId) {
        if (!Boolean.TRUE.equals(this.isReserved)) {
            throw new IllegalStateException("Alias must be reserved before assignment");
        }
        this.urlId = urlId;
    }
}