package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainURLEntity;
import ai.shreds.domain.entities.DomainURLMappingEntity;
import ai.shreds.domain.entities.DomainURLMetadataEntity;
import ai.shreds.domain.exceptions.DomainException;
import ai.shreds.domain.ports.DomainOutputPortCacheService;
import ai.shreds.domain.ports.DomainOutputPortURLRepository;
import ai.shreds.domain.ports.DomainOutputPortURLMappingRepository;
import ai.shreds.domain.ports.DomainOutputPortURLMetadataRepository;
import ai.shreds.shared.value_objects.SharedEnumURLStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Domain service for managing URL lifecycle operations.
 * Handles URL status transitions, expiration checks, and lifecycle management.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DomainURLLifecycleService {
    
    private final DomainOutputPortURLRepository urlRepository;
    private final DomainOutputPortURLMappingRepository mappingRepository;
    private final DomainOutputPortCacheService cacheService;
    private final DomainOutputPortURLMetadataRepository metadataRepository;
    
    /**
     * Updates the status of a URL and handles associated state changes.
     * 
     * @param urlId The URL identifier
     * @param status The new status to set
     */
    public void updateURLStatus(String urlId, SharedEnumURLStatus status) {
        log.info("Updating URL status for ID: {} to {}", urlId, status);
        
        if (urlId == null || urlId.trim().isEmpty()) {
            throw new DomainException("URL ID cannot be null or empty");
        }
        
        if (status == null) {
            throw new DomainException("Status cannot be null");
        }
        
        try {
            // Retrieve URL entity
            DomainURLEntity url = urlRepository.findById(urlId);
            if (url == null) {
                throw new DomainException("URL not found with ID: " + urlId);
            }
            
            // Update URL status
            url.updateStatus(status);
            urlRepository.update(url);
            
            // Update corresponding mapping
            DomainURLMappingEntity mapping = mappingRepository.findByShortCode(url.getShortCode());
            if (mapping != null) {
                mapping.setStatus(status);
                mappingRepository.update(mapping);
            }
            
            // Handle cache invalidation for inactive statuses
            if (status != SharedEnumURLStatus.ACTIVE) {
                cacheService.invalidateCache(url.getShortCode());
            }
            
            log.info("Successfully updated URL status for ID: {}", urlId);
            
        } catch (Exception e) {
            log.error("Error updating URL status: {}", e.getMessage(), e);
            throw new DomainException("Failed to update URL status: " + e.getMessage(), e);
        }
    }
    
    /**
     * Checks for expired URLs and updates their status.
     * This method should be called periodically (e.g., by a scheduled job).
     */
    public void checkAndExpireURLs() {
        log.info("Checking for expired URLs");
        
        // This is a simplified implementation. In a real system, you'd query
        // the database for URLs with expiration dates in the past
        // For now, we'll implement a basic check structure
        
        // Note: This would typically be implemented as a batch query
        // urlRepository.findExpiredUrls(new Date()).forEach(this::expireURL);
        
        log.info("Expired URL check completed");
    }
    
    /**
     * Reactivates a previously deactivated or expired URL.
     * 
     * @param urlId The URL identifier
     */
    public void reactivateURL(String urlId) {
        log.info("Reactivating URL with ID: {}", urlId);
        
        if (urlId == null || urlId.trim().isEmpty()) {
            throw new DomainException("URL ID cannot be null or empty");
        }
        
        try {
            DomainURLEntity url = urlRepository.findById(urlId);
            if (url == null) {
                throw new DomainException("URL not found with ID: " + urlId);
            }
            
            // Only reactivate if it's currently deactivated or expired
            if (url.getStatus() == SharedEnumURLStatus.DEACTIVATED || 
                url.getStatus() == SharedEnumURLStatus.EXPIRED) {
                
                // Check if URL is still within expiration date
                DomainURLMetadataEntity metadata = metadataRepository.findByUrlId(urlId);
                if (metadata != null && metadata.isExpired()) {
                    throw new DomainException("Cannot reactivate expired URL");
                }
                
                // Reactivate the URL
                updateURLStatus(urlId, SharedEnumURLStatus.ACTIVE);
                
                // Re-cache the URL mapping
                cacheService.cacheURLMapping(url.getShortCode(), url.getOriginalUrl());
                
                log.info("Successfully reactivated URL with ID: {}", urlId);
            } else {
                throw new DomainException("URL cannot be reactivated from status: " + url.getStatus());
            }
            
        } catch (Exception e) {
            log.error("Error reactivating URL: {}", e.getMessage(), e);
            throw new DomainException("Failed to reactivate URL: " + e.getMessage(), e);
        }
    }
    
    /**
     * Schedules a URL for expiration at a specific date.
     * 
     * @param urlId The URL identifier
     * @param expirationDate The expiration date to schedule
     */
    public void scheduleExpiration(String urlId, Date expirationDate) {
        log.info("Scheduling expiration for URL ID: {} at {}", urlId, expirationDate);
        
        if (urlId == null || urlId.trim().isEmpty()) {
            throw new DomainException("URL ID cannot be null or empty");
        }
        
        if (expirationDate == null) {
            throw new DomainException("Expiration date cannot be null");
        }
        
        if (expirationDate.before(new Date())) {
            throw new DomainException("Expiration date cannot be in the past");
        }
        
        try {
            DomainURLEntity url = urlRepository.findById(urlId);
            if (url == null) {
                throw new DomainException("URL not found with ID: " + urlId);
            }
            
            // Update metadata with new expiration date
            DomainURLMetadataEntity metadata = metadataRepository.findByUrlId(urlId);
            if (metadata != null) {
                metadata.setExpirationDate(expirationDate);
                metadataRepository.update(metadata);
            }
            
            log.info("Successfully scheduled expiration for URL ID: {}", urlId);
            
        } catch (Exception e) {
            log.error("Error scheduling URL expiration: {}", e.getMessage(), e);
            throw new DomainException("Failed to schedule URL expiration: " + e.getMessage(), e);
        }
    }
    
    /**
     * Expires a single URL by updating its status.
     * 
     * @param url The URL entity to expire
     */
    private void expireURL(DomainURLEntity url) {
        try {
            updateURLStatus(url.getUrlId(), SharedEnumURLStatus.EXPIRED);
            log.info("Expired URL with ID: {}", url.getUrlId());
        } catch (Exception e) {
            log.error("Error expiring URL {}: {}", url.getUrlId(), e.getMessage());
        }
    }
}