package ai.shreds.shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for QR code generation operations.
 * Contains information about the generated QR code.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedQRCodeResponseDTO {
    /** Whether the QR code generation was successful. */
    private Boolean success;
    
    /** URL where the generated QR code can be accessed. */
    private String qrCodeUrl;
    
    /** ISO-8601 timestamp when the QR code expires. */
    private String expiresAt;
}