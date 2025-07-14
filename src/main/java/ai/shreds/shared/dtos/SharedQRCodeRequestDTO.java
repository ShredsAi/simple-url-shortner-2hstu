package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for generating QR codes for shortened URLs.
 * Contains all necessary information for QR code generation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedQRCodeRequestDTO {
    /** Content to encode in the QR code (e.g., shortened URL). */
    private String content;
    
    /** Size (pixels) of the generated QR code. */
    private Integer size;
    
    /** Error correction level (e.g., L, M, Q, H). */
    private String errorCorrection;
    
    /** Output format (e.g., png, svg). */
    private String format;
    
    // Manual getters and setters to fix compilation issues
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
    
    public String getErrorCorrection() {
        return errorCorrection;
    }
    
    public void setErrorCorrection(String errorCorrection) {
        this.errorCorrection = errorCorrection;
    }
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
}