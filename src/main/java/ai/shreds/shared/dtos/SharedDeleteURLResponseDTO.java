package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for delete URL response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedDeleteURLResponseDTO {
    /** Whether deletion was successful */
    private Boolean success;
    /** Message describing outcome */
    private String message;
    /** ID of the URL deleted */
    private String urlId;
    /** Type of deletion (e.g., SOFT, HARD) */
    private String deletionType;

    // Manual getters and setters to fix compilation issues
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrlId() {
        return urlId;
    }

    public void setUrlId(String urlId) {
        this.urlId = urlId;
    }

    public String getDeletionType() {
        return deletionType;
    }

    public void setDeletionType(String deletionType) {
        this.deletionType = deletionType;
    }
}
