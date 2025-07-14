package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainURLEntity;
import ai.shreds.domain.entities.DomainURLMappingEntity;
import ai.shreds.domain.entities.DomainURLMetadataEntity;
import ai.shreds.domain.entities.DomainCustomAliasEntity;
import ai.shreds.domain.exceptions.DomainException;
import ai.shreds.domain.ports.DomainInputPortAliasService;
import ai.shreds.domain.ports.DomainInputPortURLService;
import ai.shreds.domain.ports.DomainOutputPortCacheService;
import ai.shreds.domain.ports.DomainOutputPortURLRepository;
import ai.shreds.domain.ports.DomainOutputPortURLMappingRepository;
import ai.shreds.domain.ports.DomainOutputPortURLMetadataRepository;
import ai.shreds.domain.value_objects.DomainOriginalURLValue;
import ai.shreds.domain.value_objects.DomainShortCodeValue;
import ai.shreds.domain.value_objects.DomainURLIdentifierValue;
import ai.shreds.shared.value_objects.SharedEnumURLStatus;
import ai.shreds.shared.value_objects.SharedEnumValidationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Domain service for URL shortening operations.
 * Implements core business logic for creating, retrieving, updating, and deleting shortened URLs.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DomainURLShorteningService implements DomainInputPortURLService {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();
    
    private final DomainOutputPortURLRepository urlRepository;
    private final DomainOutputPortURLMappingRepository mappingRepository;
    private final DomainOutputPortURLMetadataRepository metadataRepository;
    private final DomainOutputPortCacheService cacheService;
    private final DomainInputPortAliasService aliasService;
    
    @Override
    public DomainURLEntity createURL(String originalUrl, String shortCode, String owner, String customAlias, Date expirationDate, List<String> tags) {
        log.info("Creating URL with originalUrl: {}, customAlias: {}", originalUrl, customAlias);
        
        try {
            // Validate the original URL using value object
            DomainOriginalURLValue urlValue = new DomainOriginalURLValue(originalUrl);
            
            // Generate short code if not provided or use custom alias
            String finalShortCode;
            DomainCustomAliasEntity reservedAlias = null;
            if (customAlias != null && !customAlias.isEmpty()) {
                // Validate custom alias
                if (!aliasService.validateAlias(customAlias)) {
                    throw new DomainException("Invalid custom alias format: " + customAlias);
                }
                if (aliasService.checkReservedWords(customAlias)) {
                    throw new DomainException("Custom alias matches reserved word: " + customAlias);
                }
                // Reserve the alias
                reservedAlias = aliasService.reserveAlias(customAlias, owner);
                finalShortCode = customAlias;
            } else {
                // Generate a short code if none provided
                finalShortCode = shortCode != null ? shortCode : generateUniqueShortCode();
            }
            
            // Create domain URL entity
            String urlId = UUID.randomUUID().toString();
            DomainURLEntity url = DomainURLEntity.builder()
                    .urlId(urlId)
                    .originalUrl(urlValue.getValue())
                    .shortCode(finalShortCode)
                    .customAlias(customAlias)
                    .status(SharedEnumURLStatus.ACTIVE)
                    .owner(owner)
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .validationStatus(SharedEnumValidationStatus.PENDING)
                    .securityScore(0)
                    .build();
            
            // Save URL entity
            DomainURLEntity savedUrl = urlRepository.save(url);
            
            // Create URL mapping entity for fast lookups
            DomainURLMappingEntity mapping = DomainURLMappingEntity.builder()
                    .mappingId(UUID.randomUUID().toString())
                    .shortCode(finalShortCode)
                    .originalUrl(urlValue.getValue())
                    .urlId(urlId)
                    .status(SharedEnumURLStatus.ACTIVE)
                    .createdAt(new Date())
                    .build();
            
            mappingRepository.save(mapping);
            
            // Create URL metadata
            DomainURLMetadataEntity metadata = DomainURLMetadataEntity.builder()
                    .metadataId(UUID.randomUUID().toString())
                    .urlId(urlId)
                    .creationDate(new Date())
                    .expirationDate(expirationDate)
                    .lastAccessedDate(null)
                    .accessCount(0L)
                    .tags(tags)
                    .build();
            
            metadataRepository.save(metadata);
            
            // Assign alias to URL if reserved
            if (reservedAlias != null) {
                reservedAlias.assignToURL(urlId);
            }
            
            // Cache URL mapping
            cacheService.cacheURLMapping(finalShortCode, originalUrl);
            
            log.info("Successfully created URL with ID: {}, short code: {}", urlId, finalShortCode);
            
            return savedUrl;
        } catch (IllegalArgumentException e) {
            log.error("Invalid URL or alias format: {}", e.getMessage());
            throw new DomainException("Failed to create URL: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error creating URL: {}", e.getMessage(), e);
            throw new DomainException("Failed to create URL: " + e.getMessage(), e);
        }
    }
    
    @Override
    public DomainURLEntity getURL(String urlId) {
        log.info("Retrieving URL with ID: {}", urlId);
        
        DomainURLEntity url = urlRepository.findById(urlId);
        if (url == null) {
            throw new DomainException("URL not found with ID: " + urlId);
        }
        
        return url;
    }
    
    @Override
    public DomainURLEntity updateURL(String urlId, Date expirationDate, List<String> tags) {
        log.info("Updating URL with ID: {}", urlId);
        
        DomainURLEntity url = urlRepository.findById(urlId);
        if (url == null) {
            throw new DomainException("URL not found with ID: " + urlId);
        }
        
        // Update URL metadata
        DomainURLMetadataEntity metadata = metadataRepository.findByUrlId(urlId);
        if (metadata != null) {
            if (expirationDate != null) {
                metadata.setExpirationDate(expirationDate);
            }
            
            if (tags != null) {
                metadata.setTags(tags);
            }
            
            metadataRepository.update(metadata);
        }
        
        // Update the entity's updated timestamp
        url.setUpdatedAt(new Date());
        
        // Check if the URL should be marked as expired
        if (metadata != null && metadata.isExpired()) {
            url.updateStatus(SharedEnumURLStatus.EXPIRED);
        }
        
        return urlRepository.update(url);
    }
    
    @Override
    public Boolean deleteURL(String urlId, String deletionType) {
        log.info("Deleting URL with ID: {}, deletion type: {}", urlId, deletionType);
        
        DomainURLEntity url = urlRepository.findById(urlId);
        if (url == null) {
            throw new DomainException("URL not found with ID: " + urlId);
        }
        
        boolean isHardDelete = "HARD".equalsIgnoreCase(deletionType);
        
        if (isHardDelete) {
            // Hard delete - complete removal from the database
            urlRepository.delete(urlId);
            
            // Remove mapping
            DomainURLMappingEntity mapping = mappingRepository.findByShortCode(url.getShortCode());
            if (mapping != null) {
                mappingRepository.delete(mapping.getMappingId());
            }
            
            // Release custom alias if present
            if (url.getCustomAlias() != null && !url.getCustomAlias().isEmpty()) {
                try {
                    // Find the alias entity and release it
                    DomainCustomAliasEntity alias = aliasService.reserveAlias(url.getCustomAlias(), url.getOwner());
                    if (alias != null) {
                        aliasService.releaseAlias(alias.getAliasId());
                    }
                } catch (Exception e) {
                    log.warn("Could not release alias during deletion: {}", e.getMessage());
                    // Continue with deletion even if alias release fails
                }
            }
        } else {
            // Soft delete - mark as deleted
            url.updateStatus(SharedEnumURLStatus.DELETED);
            urlRepository.update(url);
            
            // Update mapping
            DomainURLMappingEntity mapping = mappingRepository.findByShortCode(url.getShortCode());
            if (mapping != null) {
                mapping.markDeleted();
                mappingRepository.update(mapping);
            }
        }
        
        // Invalidate cache in both cases
        cacheService.invalidateCache(url.getShortCode());
        
        log.info("Successfully deleted URL with ID: {}", urlId);
        return true;
    }
    
    /**
     * Generates a unique short code that doesn't collide with any existing short codes.
     * 
     * @return A unique short code
     * @throws DomainException if unable to generate a unique code after multiple attempts
     */
    private String generateUniqueShortCode() {
        final int maxAttempts = 10;
        String shortCode = null;
        int attempts = 0;
        
        while (shortCode == null && attempts < maxAttempts) {
            String candidate = generateRandomShortCode();
            if (!urlRepository.existsByShortCode(candidate)) {
                shortCode = candidate;
            }
            attempts++;
        }
        
        if (shortCode == null) {
            throw new DomainException("Failed to generate unique short code after " + maxAttempts + " attempts");
        }
        
        return shortCode;
    }
    
    /**
     * Generates a random short code using a secure random number generator.
     * 
     * @return A random short code
     */
    private String generateRandomShortCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(BASE62_CHARS.length());
            sb.append(BASE62_CHARS.charAt(randomIndex));
        }
        return sb.toString();
    }
}