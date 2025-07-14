package ai.shreds.domain.entities;

import ai.shreds.shared.value_objects.SharedEnumOperationStatus;
import ai.shreds.shared.value_objects.SharedEnumOperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Domain entity representing a bulk operation on multiple URLs.
 * Tracks the progress and results of batch URL operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainBulkOperationEntity {
    
    /** Unique identifier for the bulk operation */
    private String operationId;
    
    /** User who initiated the bulk operation */
    private String owner;
    
    /** Type of bulk operation (CREATE, UPDATE, DELETE) */
    private SharedEnumOperationType operationType;
    
    /** Total number of URLs in the request */
    private Integer totalRequested;
    
    /** Number of successfully processed URLs */
    private Integer successful;
    
    /** Number of failed URL operations */
    private Integer failed;
    
    /** Timestamp when the operation started */
    private Date startTime;
    
    /** Timestamp when the operation completed */
    private Date endTime;
    
    /** Current status of the operation */
    private SharedEnumOperationStatus status;
    
    /** References to successfully created/updated URLs */
    @Builder.Default
    private List<String> resultUrls = new ArrayList<>();
    
    /** Details of failed operations including error messages */
    @Builder.Default
    private List<Map<String, String>> errorDetails = new ArrayList<>();
    
    /**
     * Marks the bulk operation as complete and sets the end time.
     */
    public void complete() {
        this.status = SharedEnumOperationStatus.COMPLETED;
        this.endTime = new Date();
    }
    
    /**
     * Adds a successfully processed URL to the results.
     * 
     * @param urlId The ID of the successfully processed URL
     */
    public void addSuccess(String urlId) {
        if (this.successful == null) {
            this.successful = 0;
        }
        this.successful++;
        this.resultUrls.add(urlId);
    }
    
    /**
     * Adds a failed URL operation to the error details.
     * 
     * @param url The URL that failed processing
     * @param error The error message
     */
    public void addFailure(String url, String error) {
        if (this.failed == null) {
            this.failed = 0;
        }
        this.failed++;
        
        Map<String, String> errorDetail = new HashMap<>();
        errorDetail.put("url", url);
        errorDetail.put("error", error);
        this.errorDetails.add(errorDetail);
    }
}