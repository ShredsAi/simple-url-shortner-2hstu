package ai.shreds.domain.entities;

import ai.shreds.shared.dtos.SharedURLMetadataDTO;
import ai.shreds.shared.dtos.SharedURLResponseDTO;
import ai.shreds.shared.value_objects.SharedEnumURLStatus;
import ai.shreds.shared.value_objects.SharedEnumValidationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Domain entity representing a shortened URL.
 * Contains all the core business logic and rules for URL management.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainURLEntity {
    
    /** Unique identifier for the URL entity */
    private String urlId;
    
    /** The original long URL that was shortened */
    private String originalUrl;
    
    /** Generated short code or custom alias used in the shortened URL */
    private String shortCode;
    
    /** Current status of the URL */
    private SharedEnumURLStatus status;
    
    /** Identifier of the user who created the shortened URL */
    private String owner;
    
    /** Timestamp when the URL was created */
    private Date createdAt;
    
    /** Timestamp when the URL was last updated */
    private Date updatedAt;
    
    /** Status of security validation */
    private SharedEnumValidationStatus validationStatus;
    
    /** Security rating of the URL from 0-100 */
    private Integer securityScore;
    
    /** Custom alias if provided by the user */
    private String customAlias;
    
    // Manual getters and setters to fix compilation issues
    public String getUrlId() {
        return urlId;
    }
    
    public void setUrlId(String urlId) {
        this.urlId = urlId;
    }
    
    public String getOriginalUrl() {
        return originalUrl;
    }
    
    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
    
    public String getShortCode() {
        return shortCode;
    }
    
    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }
    
    public SharedEnumURLStatus getStatus() {
        return status;
    }
    
    public void setStatus(SharedEnumURLStatus status) {
        this.status = status;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public SharedEnumValidationStatus getValidationStatus() {
        return validationStatus;
    }
    
    public void setValidationStatus(SharedEnumValidationStatus validationStatus) {
        this.validationStatus = validationStatus;
    }
    
    public Integer getSecurityScore() {
        return securityScore;
    }
    
    public void setSecurityScore(Integer securityScore) {
        this.securityScore = securityScore;
    }
    
    public String getCustomAlias() {
        return customAlias;
    }
    
    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }
    
    /**
     * Checks if the URL has expired based on its status.
     * 
     * @return true if the URL is expired
     */
    public boolean isExpired() {
        return SharedEnumURLStatus.EXPIRED.equals(this.status);
    }
    
    /**
     * Checks if the URL can be accessed by the specified user.
     * 
     * @param userId The ID of the user trying to access the URL
     * @return true if the user can access the URL
     */
    public boolean canBeAccessedBy(String userId) {
        return this.owner != null && this.owner.equals(userId);
    }
    
    /**
     * Updates the status of the URL and sets the updated timestamp.
     * 
     * @param status The new status to set
     */
    public void updateStatus(SharedEnumURLStatus status) {
        this.status = status;
        this.updatedAt = new Date();
    }
    
    /**
     * Updates the validation state of the URL.
     * 
     * @param validationStatus The new validation status
     * @param securityScore The security score from validation
     */
    public void updateValidationState(SharedEnumValidationStatus validationStatus, Integer securityScore) {
        this.validationStatus = validationStatus;
        this.securityScore = securityScore;
        this.updatedAt = new Date();
    }
    
    /**
     * Converts the entity to a response DTO.
     * 
     * @param metadata The metadata entity to include
     * @param shortenedUrl The full shortened URL
     * @param qrCodeUrl The QR code URL if available
     * @return SharedURLResponseDTO representation of this entity
     */
    public SharedURLResponseDTO toDTO(DomainURLMetadataEntity metadata, String shortenedUrl, String qrCodeUrl) {
        return SharedURLResponseDTO.builder()
                .urlId(this.urlId)
                .originalUrl(this.originalUrl)
                .shortCode(this.shortCode)
                .shortenedUrl(shortenedUrl)
                .customAlias(this.customAlias)
                .metadata(metadata != null ? metadata.toDTO() : null)
                .qrCodeUrl(qrCodeUrl)
                .status(this.status)
                .build();
    }
}