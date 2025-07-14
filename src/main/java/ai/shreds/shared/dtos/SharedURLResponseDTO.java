package ai.shreds.shared.dtos;

import ai.shreds.shared.value_objects.SharedEnumURLStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the response for URL operations.
 * Contains all relevant information about a shortened URL.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedURLResponseDTO {
    
    /** Unique identifier of the URL. */
    private String urlId;

    /** Original long URL that was shortened. */
    private String originalUrl;

    /** Generated short code or alias used in the shortened URL. */
    private String shortCode;

    /** Full shortened URL (including domain). */
    private String shortenedUrl;

    /** Custom alias if provided by the user. */
    private String customAlias;

    /** Associated metadata information including creation date, expiration, access count, etc. */
    private SharedURLMetadataDTO metadata;

    /** URL to the generated QR code, if requested. */
    private String qrCodeUrl;

    /** Current status of the URL (ACTIVE, EXPIRED, DEACTIVATED, DELETED). */
    private SharedEnumURLStatus status;

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

    public String getShortenedUrl() {
        return shortenedUrl;
    }

    public void setShortenedUrl(String shortenedUrl) {
        this.shortenedUrl = shortenedUrl;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }

    public SharedURLMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(SharedURLMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public SharedEnumURLStatus getStatus() {
        return status;
    }

    public void setStatus(SharedEnumURLStatus status) {
        this.status = status;
    }
}
