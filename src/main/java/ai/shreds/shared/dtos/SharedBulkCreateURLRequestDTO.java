package ai.shreds.shared.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotEmpty;

/**
 * DTO for bulk creating shortened URLs.
 * Allows creating multiple URLs in a single request with common settings.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedBulkCreateURLRequestDTO {

    /**
     * Array of URL creation requests to be processed in bulk
     * Cannot be empty - at least one URL request is required
     */
    @NotEmpty(message = "urls list cannot be empty")
    private SharedCreateURLRequestDTO[] urls;

    /**
     * Optional default expiration date in ISO-8601 format
     * Applied to all URLs in the bulk request that don't have explicit expiration date
     */
    private String defaultExpirationDate;

    /**
     * Flag indicating if QR codes should be generated for all URLs in the bulk request
     * Overrides individual generateQRCode settings in each URL request
     */
    private Boolean generateQRCodes;
}