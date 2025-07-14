package ai.shreds.application.services;

import ai.shreds.application.ports.ApplicationOutputPortEventPublisher;
import ai.shreds.application.ports.ApplicationOutputPortAnalyticsService;
import ai.shreds.shared.dtos.SharedURLResponseDTO;
import ai.shreds.shared.dtos.SharedURLCreatedEventDTO;
import ai.shreds.shared.dtos.SharedURLUpdatedEventDTO;
import ai.shreds.shared.dtos.SharedURLDeletedEventDTO;
import ai.shreds.shared.dtos.SharedURLMetadataDTO;
import ai.shreds.shared.dtos.SharedBulkOperationCompletedEventDTO;
import ai.shreds.shared.dtos.SharedURLValidationRequestEventDTO;
import ai.shreds.shared.dtos.SharedValidationResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationEventPublishingService {

    private final ApplicationOutputPortEventPublisher eventPublisher;
    private final ApplicationOutputPortAnalyticsService analyticsService;

    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    /**
     * Publishes events when a URL is created.
     * 
     * @param urlData The URL response data
     */
    public void publishURLCreated(SharedURLResponseDTO urlData) {
        log.info("Publishing URL created event for URL ID: {}", urlData.getUrlId());
        
        // Create and publish the event
        SharedURLCreatedEventDTO event = new SharedURLCreatedEventDTO();
        event.setUrlId(urlData.getUrlId());
        event.setShortCode(urlData.getShortCode());
        event.setOriginalUrl(urlData.getOriginalUrl());
        event.setOwner(getCurrentUserId()); // Get from security context in a real implementation
        event.setCreationTimestamp(ISO_DATE_FORMAT.format(new Date()));
        event.setMetadata(urlData.getMetadata());
        
        eventPublisher.publishURLCreatedEvent(event);
        
        // Track for analytics
        analyticsService.trackURLCreated(event);
    }

    /**
     * Publishes events when a URL is updated.
     * 
     * @param urlData The updated URL data
     */
    public void publishURLUpdated(SharedURLResponseDTO urlData) {
        log.info("Publishing URL updated event for URL ID: {}", urlData.getUrlId());
        
        // Create and publish the event
        SharedURLUpdatedEventDTO event = new SharedURLUpdatedEventDTO();
        event.setUrlId(urlData.getUrlId());
        event.setShortCode(urlData.getShortCode());
        event.setUpdatedFields(determineUpdatedFields(urlData));
        event.setUpdateTimestamp(ISO_DATE_FORMAT.format(new Date()));
        event.setOwner(getCurrentUserId());
        
        eventPublisher.publishURLUpdatedEvent(event);
        
        // Track for analytics
        analyticsService.trackURLUpdated(event);
    }

    /**
     * Publishes events when a URL is deleted.
     * 
     * @param urlId The ID of the deleted URL
     * @param deletionType The type of deletion (e.g. "SOFT_DELETE" or "HARD_DELETE")
     */
    public void publishURLDeleted(String urlId, String deletionType) {
        log.info("Publishing URL deleted event for URL ID: {}, deletion type: {}", urlId, deletionType);
        
        // Create and publish the event
        SharedURLDeletedEventDTO event = new SharedURLDeletedEventDTO();
        event.setUrlId(urlId);
        event.setShortCode(null); // This would be retrieved from the URL entity before deletion in a real implementation
        event.setDeletionType(deletionType);
        event.setDeletionTimestamp(ISO_DATE_FORMAT.format(new Date()));
        event.setOwner(getCurrentUserId());
        
        eventPublisher.publishURLDeletedEvent(event);
        
        // Track for analytics
        analyticsService.trackURLDeleted(event);
    }

    /**
     * Publishes events when a bulk operation is completed.
     * 
     * @param operation The completed bulk operation data
     */
    public void publishBulkOperationCompleted(SharedBulkOperationCompletedEventDTO operation) {
        log.info("Publishing bulk operation completed event for operation type: {}, success rate: {}/{}", 
                operation.getOperationType(), operation.getSuccessful(), operation.getTotalRequested());
        
        eventPublisher.publishBulkOperationCompletedEvent(operation);
    }

    /**
     * Requests validation for a URL.
     * 
     * @param urlId The URL ID to validate
     * @param originalUrl The original URL to validate
     */
    public void requestURLValidation(String urlId, String originalUrl) {
        log.info("Requesting validation for URL ID: {}, original URL: {}", urlId, originalUrl);
        
        SharedURLValidationRequestEventDTO event = new SharedURLValidationRequestEventDTO();
        event.setUrlId(urlId);
        event.setOriginalUrl(originalUrl);
        event.setValidationPriority(determineValidationPriority(originalUrl));
        event.setRequestTimestamp(ISO_DATE_FORMAT.format(new Date()));
        
        eventPublisher.publishURLValidationRequestEvent(event);
    }

    /**
     * Notify about a rejected URL after validation.
     * This is an additional method beyond the core interfaces.
     * 
     * @param urlId The rejected URL ID
     * @param validationResult The validation result
     */
    public void notifyURLRejected(String urlId, SharedValidationResultDTO validationResult) {
        log.warn("URL ID {} was rejected with threats: {}", urlId, String.join(", ", validationResult.getThreats()));
        // In a real implementation, this might publish to a specific "url.rejected" topic
        // or send notifications to admins/security teams
    }

    /**
     * Determine which fields were updated in a URL update operation.
     * In a real implementation, this would compare old and new values.
     */
    private String[] determineUpdatedFields(SharedURLResponseDTO urlData) {
        // Simplified implementation, in reality would compare previous and current state
        return new String[] {"expirationDate", "tags"};
    }

    /**
     * Determine the priority for URL validation.
     * This could be based on URL characteristics, user tier, etc.
     */
    private String determineValidationPriority(String originalUrl) {
        // Simple logic - could be more sophisticated in a real implementation
        if (originalUrl.contains("payment") || originalUrl.contains("login")) {
            return "HIGH";
        }
        return "NORMAL";
    }

    /**
     * Get the current user ID from the security context.
     * Placeholder implementation.
     */
    private String getCurrentUserId() {
        // In a real implementation, this would get the user ID from Spring Security context
        return "user123";
    }
}