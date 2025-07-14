package ai.shreds.shared.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for creating a single shortened URL request.
 * Contains all necessary information to create a shortened URL.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedCreateURLRequestDTO {

    /**
     * The original URL to be shortened
     * Required field that must not be blank
     */
    @NotBlank(message = "originalUrl is required")
    private String originalUrl;

    /**
     * Optional custom alias for the shortened URL
     * If provided, the system will try to use this alias instead of generating a random code
     */
    private String customAlias;

    /**
     * Optional expiration date in ISO-8601 format
     * If not provided, the system default expiration will be used
     */
    private String expirationDate;

    /**
     * Optional tags for categorization and organization of URLs
     */
    private String[] tags;

    /**
     * Flag indicating if a QR code should be generated for this URL
     * Defaults to false if not specified
     */
    private Boolean generateQRCode;

    // Manual getters and setters to fix compilation issues
    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }

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

    public Boolean getGenerateQRCode() {
        return generateQRCode;
    }

    public void setGenerateQRCode(Boolean generateQRCode) {
        this.generateQRCode = generateQRCode;
    }
}
