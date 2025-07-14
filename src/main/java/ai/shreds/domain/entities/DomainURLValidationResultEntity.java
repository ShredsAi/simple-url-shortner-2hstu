package ai.shreds.domain.entities;

import ai.shreds.shared.dtos.SharedValidationResultDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Domain entity representing URL validation results.
 * Contains security assessment and content categorization information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainURLValidationResultEntity {
    
    /** Unique identifier for the validation result */
    private String validationId;
    
    /** Reference to the validated URL entity */
    private String urlId;
    
    /** Overall validation result */
    private Boolean isValid;
    
    /** Security rating from 0-100 */
    private Integer securityScore;
    
    /** Detected security threats if any */
    private List<String> threats;
    
    /** Content categories the URL belongs to */
    private List<String> categories;
    
    /** Timestamp when validation was performed */
    private Date validationDate;
    
    /** Identifier of the system that performed validation */
    private String validationSource;
    
    /** Detailed validation results and findings */
    private Map<String, Object> validationDetails;
    
    /**
     * Determines if the URL is safe based on validation results.
     * 
     * @return true if the URL is considered safe
     */
    public boolean isSafe() {
        // Consider URLs safe if they're valid, have no threats and a security score above 70
        return Boolean.TRUE.equals(this.isValid) && 
               (this.threats == null || this.threats.isEmpty()) && 
               this.securityScore != null && this.securityScore >= 70;
    }
    
    /**
     * Converts the entity to a DTO for external communication.
     * 
     * @return SharedValidationResultDTO representation of this entity
     */
    public SharedValidationResultDTO toDTO() {
        return SharedValidationResultDTO.builder()
                .isValid(this.isValid)
                .securityScore(this.securityScore)
                .threats(this.threats != null ? this.threats.toArray(new String[0]) : new String[0])
                .categories(this.categories != null ? this.categories.toArray(new String[0]) : new String[0])
                .build();
    }
}