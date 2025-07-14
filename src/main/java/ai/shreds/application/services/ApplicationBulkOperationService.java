package ai.shreds.application.services;

import ai.shreds.application.exceptions.*;
import ai.shreds.application.ports.*;
import ai.shreds.domain.ports.*;
import ai.shreds.shared.dtos.*;
import ai.shreds.shared.value_objects.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationBulkOperationService implements ApplicationInputPortBulkCreateURLs {

    private final ApplicationInputPortCreateURL urlService;
    private final ApplicationQuotaValidationService quotaValidationService;
    private final DomainInputPortBulkOperationService domainBulkOperationService;
    private final ApplicationEventPublishingService eventPublishingService;

    @Override
    @Transactional
    public SharedBulkOperationResponseDTO bulkCreateURLs(SharedBulkCreateURLRequestDTO request) {
        String userId = getCurrentUserId();
        int requestedCount = request.getUrls().length;
        
        log.info("Processing bulk URL creation request for user: {}, requested count: {}", userId, requestedCount);
        
        // Validate quota
        if (!quotaValidationService.validateQuota(userId, requestedCount)) {
            throw new ApplicationQuotaExceededException(userId, requestedCount, quotaValidationService.getUserRemainingQuota(userId));
        }
        
        // Check if user can perform bulk operations
        if (!quotaValidationService.canPerformBulkOperations(userId)) {
            log.warn("User {} attempted bulk operation without permission", userId);
            throw new ApplicationAccessDeniedException(null, userId);
        }
        
        try {
            // Create bulk operation entity
            List<Map<String, Object>> urlsData = new ArrayList<>();
            for (SharedCreateURLRequestDTO urlRequest : request.getUrls()) {
                Map<String, Object> urlData = new HashMap<>();
                urlData.put("originalUrl", urlRequest.getOriginalUrl());
                urlData.put("customAlias", urlRequest.getCustomAlias());
                urlData.put("expirationDate", urlRequest.getExpirationDate() != null ? 
                    urlRequest.getExpirationDate() : request.getDefaultExpirationDate());
                urlData.put("tags", urlRequest.getTags());
                urlData.put("generateQRCode", request.getGenerateQRCodes());
                urlsData.add(urlData);
            }
            
            var bulkOperation = domainBulkOperationService.createBulkOperation(
                userId,
                SharedEnumOperationType.CREATE,
                urlsData
            );
            
            // Process bulk operation
            var processedOperation = domainBulkOperationService.processBulkURLCreation(
                bulkOperation,
                urlsData
            );
            
            // Prepare response
            var response = new SharedBulkOperationResponseDTO();
            response.setTotalRequested(processedOperation.getTotalRequested());
            response.setSuccessful(processedOperation.getSuccessful());
            response.setFailed(processedOperation.getFailed());
            
            // Convert result URLs to response DTOs
            List<SharedURLResponseDTO> results = new ArrayList<>();
            for (String urlId : processedOperation.getResultUrls()) {
                try {
                    var urlResponse = urlService.getURL(urlId);
                    results.add(urlResponse);
                } catch (Exception e) {
                    log.warn("Unable to fetch URL data for successful URL: {}", urlId, e);
                    // Skip failed URLs in the results list
                }
            }
            response.setResults(results.toArray(new SharedURLResponseDTO[0]));
            
            // Convert error details to error DTOs
            List<SharedBulkOperationErrorDTO> errors = new ArrayList<>();
            for (Map<String, String> errorDetail : processedOperation.getErrorDetails()) {
                var errorDTO = new SharedBulkOperationErrorDTO();
                errorDTO.setUrl(errorDetail.get("url"));
                errorDTO.setError(errorDetail.get("error"));
                errors.add(errorDTO);
            }
            response.setErrors(errors.toArray(new SharedBulkOperationErrorDTO[0]));
            
            // Publish bulk operation completed event
            var completedEvent = new SharedBulkOperationCompletedEventDTO();
            completedEvent.setOperationId(processedOperation.getOperationId());
            completedEvent.setOperationType("CREATE");
            completedEvent.setTotalRequested(processedOperation.getTotalRequested());
            completedEvent.setSuccessful(processedOperation.getSuccessful());
            completedEvent.setFailed(processedOperation.getFailed());
            completedEvent.setOwner(userId);
            completedEvent.setCompletionTimestamp(new Date().toString());
            
            eventPublishingService.publishBulkOperationCompleted(completedEvent);
            
            log.info("Bulk URL creation completed for user: {}, success rate: {}/{}", 
                    userId, processedOperation.getSuccessful(), requestedCount);
            
            return response;
        } catch (Exception e) {
            log.error("Failed to process bulk URL creation for user: {}", userId, e);
            throw new ApplicationException("Failed to process bulk URL creation: " + e.getMessage(), e);
        }
    }
    
    private String getCurrentUserId() {
        // TODO: Get from Spring Security context
        return "user123"; // Placeholder
    }
}