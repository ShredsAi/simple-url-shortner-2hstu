package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainBulkOperationEntity;
import ai.shreds.domain.entities.DomainURLEntity;
import ai.shreds.domain.exceptions.DomainException;
import ai.shreds.domain.ports.DomainInputPortBulkOperationService;
import ai.shreds.domain.ports.DomainInputPortURLService;
import ai.shreds.domain.ports.DomainOutputPortBulkOperationRepository;
import ai.shreds.shared.value_objects.SharedEnumOperationStatus;
import ai.shreds.shared.value_objects.SharedEnumOperationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain service for bulk URL operations.
 * Manages transaction handling, error recovery, and batch processing of URL operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DomainBulkOperationService implements DomainInputPortBulkOperationService {

    private final DomainOutputPortBulkOperationRepository bulkRepository;
    private final DomainInputPortURLService urlService;
    
    @Override
    public DomainBulkOperationEntity createBulkOperation(String owner, SharedEnumOperationType operationType, List<Map<String, Object>> urls) {
        log.info("Creating bulk operation of type {} with {} URLs for owner {}", operationType, urls.size(), owner);
        
        try {
            // Create bulk operation entity
            DomainBulkOperationEntity operation = DomainBulkOperationEntity.builder()
                    .operationId(UUID.randomUUID().toString())
                    .owner(owner)
                    .operationType(operationType)
                    .totalRequested(urls.size())
                    .successful(0)
                    .failed(0)
                    .startTime(new Date())
                    .status(SharedEnumOperationStatus.PENDING)
                    .build();
            
            // Save initial state
            operation = bulkRepository.save(operation);
            
            log.info("Created bulk operation with ID: {}", operation.getOperationId());
            return operation;
        } catch (Exception e) {
            log.error("Error creating bulk operation: {}", e.getMessage(), e);
            throw new DomainException("Failed to create bulk operation: " + e.getMessage(), e);
        }
    }
    
    @Override
    public DomainBulkOperationEntity processBulkURLCreation(DomainBulkOperationEntity operation, List<Map<String, Object>> urls) {
        log.info("Processing bulk URL creation for operation ID: {}", operation.getOperationId());
        
        if (operation == null) {
            throw new DomainException("Bulk operation cannot be null");
        }
        
        if (urls == null || urls.isEmpty()) {
            throw new DomainException("URL list cannot be null or empty");
        }
        
        try {
            // Update operation status
            operation.setStatus(SharedEnumOperationStatus.IN_PROGRESS);
            bulkRepository.update(operation);
            
            // Process each URL individually
            for (Map<String, Object> urlData : urls) {
                processSingleURL(urlData, operation);
            }
            
            // Complete the operation
            operation.setStatus(SharedEnumOperationStatus.COMPLETED);
            operation.setEndTime(new Date());
            
            // Save final state
            operation = bulkRepository.update(operation);
            
            log.info("Completed bulk URL creation for operation ID: {}, successful: {}, failed: {}", 
                     operation.getOperationId(), operation.getSuccessful(), operation.getFailed());
            
            return operation;
        } catch (Exception e) {
            log.error("Error processing bulk URL creation: {}", e.getMessage(), e);
            
            // Mark operation as failed but preserve partial results
            operation.setStatus(SharedEnumOperationStatus.FAILED);
            operation.setEndTime(new Date());
            bulkRepository.update(operation);
            
            throw new DomainException("Failed to process bulk URL creation: " + e.getMessage(), e);
        }
    }
    
    /**
     * Processes a single URL within a bulk operation.
     * 
     * @param urlData The URL data map containing parameters for URL creation
     * @param operation The parent bulk operation entity
     */
    private void processSingleURL(Map<String, Object> urlData, DomainBulkOperationEntity operation) {
        try {
            // Extract URL data from the map
            String originalUrl = (String) urlData.get("originalUrl");
            String customAlias = (String) urlData.get("customAlias");
            String owner = operation.getOwner();
            Date expirationDate = urlData.get("expirationDate") instanceof Date ? 
                    (Date) urlData.get("expirationDate") : 
                    (urlData.get("defaultExpirationDate") instanceof Date ? 
                            (Date) urlData.get("defaultExpirationDate") : null);
            
            @SuppressWarnings("unchecked")
            List<String> tags = (List<String>) urlData.get("tags");
            
            // Skip if original URL is missing
            if (originalUrl == null || originalUrl.trim().isEmpty()) {
                operation.addFailure("<missing URL>", "Original URL cannot be null or empty");
                return;
            }
            
            // Create URL
            DomainURLEntity url = urlService.createURL(
                    originalUrl,
                    null, // Auto-generate short code
                    owner,
                    customAlias,
                    expirationDate,
                    tags
            );
            
            // Record success
            operation.addSuccess(url.getUrlId());
            log.info("Successfully created URL ID: {} in bulk operation", url.getUrlId());
            
        } catch (Exception e) {
            // Record failure but continue processing
            String url = urlData != null ? (String) urlData.get("originalUrl") : "<unknown>";
            operation.addFailure(url, e.getMessage());
            log.warn("Failed to create URL {} in bulk operation: {}", url, e.getMessage());
        }
        
        // Regularly update the operation status to preserve progress
        if ((operation.getSuccessful() + operation.getFailed()) % 10 == 0) {
            bulkRepository.update(operation);
        }
    }
}