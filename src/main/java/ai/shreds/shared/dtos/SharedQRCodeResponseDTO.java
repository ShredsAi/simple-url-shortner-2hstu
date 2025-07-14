package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for QR code generation results.
 * Contains the generated QR code information and metadata.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedQRCodeResponseDTO {
    /** Indicates if QR code generation was successful. */
    private Boolean success;
    
    /** URL or base64 encoded data of the generated QR code. */
    private String qrCodeUrl;
    
    /** ISO-8601 timestamp when the QR code expires. */
    private String expiresAt;
    
    // Manual getters and setters to fix compilation issues
    public Boolean getSuccess() {
        return success;
    }
    
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    
    public String getQrCodeUrl() {
        return qrCodeUrl;
    }
    
    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
    
    public String getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }
}