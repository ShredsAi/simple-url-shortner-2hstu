package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainURLEntity;

import java.util.Date;
import java.util.List;

/**
 * Domain input port for URL management operations.
 * This interface defines the contract for URL-related business operations.
 */
public interface DomainInputPortURLService {
    
    /**
     * Creates a new shortened URL with the given parameters.
     * 
     * @param originalUrl The original long URL to be shortened
     * @param shortCode The short code to use (can be generated if null)
     * @param owner The owner/creator of the URL
     * @param customAlias Optional custom alias for the URL
     * @param expirationDate Optional expiration date for the URL
     * @param tags Optional tags for URL categorization
     * @return The created URL entity
     */
    DomainURLEntity createURL(String originalUrl, String shortCode, String owner, String customAlias, Date expirationDate, List<String> tags);
    
    /**
     * Retrieves a URL by its unique identifier.
     * 
     * @param urlId The unique identifier of the URL
     * @return The URL entity if found
     */
    DomainURLEntity getURL(String urlId);
    
    /**
     * Updates an existing URL's metadata.
     * 
     * @param urlId The unique identifier of the URL to update
     * @param expirationDate New expiration date (can be null to keep current)
     * @param tags New tags (can be null to keep current)
     * @return The updated URL entity
     */
    DomainURLEntity updateURL(String urlId, Date expirationDate, List<String> tags);
    
    /**
     * Deletes a URL (soft or hard deletion).
     * 
     * @param urlId The unique identifier of the URL to delete
     * @param deletionType The type of deletion ("SOFT" or "HARD")
     * @return true if deletion was successful
     */
    Boolean deleteURL(String urlId, String deletionType);
}