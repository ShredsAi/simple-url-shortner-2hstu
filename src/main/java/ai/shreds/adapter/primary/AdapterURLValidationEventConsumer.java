package ai.shreds.adapter.primary;

import ai.shreds.application.services.ApplicationURLValidationService;
import ai.shreds.shared.dtos.SharedURLValidationCompletedEventDTO;
import ai.shreds.adapter.exceptions.AdapterValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Event consumer for processing URL validation completion events.
 * Listens to validation events from the URL Security & Validation Shred
 * and processes them through the application layer.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdapterURLValidationEventConsumer {

    private final ApplicationURLValidationService validationService;

    /**
     * Consumes URL validation completed events from the message queue.
     * This method is triggered when the URL Security & Validation Shred
     * publishes a validation completion event.
     *
     * @param event The validation completion event containing urlId, validationResult, and timestamp
     */
    @RabbitListener(queues = "url.validation.completed")
    public void consumeValidationCompletedEvent(SharedURLValidationCompletedEventDTO event) {
        log.info("Received validation completed event for URL ID: {}, isValid: {}, securityScore: {}", 
                event.getUrlId(), 
                event.getValidationResult().getIsValid(), 
                event.getValidationResult().getSecurityScore());
        
        try {
            // Validate the event data
            validateEvent(event);
            
            // Process the validation result through the application layer
            validationService.processValidationResult(event);
            
            log.info("Successfully processed validation result for URL ID: {}", event.getUrlId());
            
        } catch (AdapterValidationException e) {
            log.error("Validation error processing event for URL ID {}: {}", 
                    event.getUrlId(), e.getMessage());
            // In a real implementation, you might want to send this to a dead letter queue
            // or implement retry logic
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error processing validation event for URL ID {}: {}", 
                    event.getUrlId(), e.getMessage(), e);
            throw new AdapterValidationException(
                    "Failed to process validation event: " + e.getMessage(), 
                    event.getUrlId(), 
                    e);
        }
    }

    /**
     * Validates the incoming event data to ensure it's complete and valid.
     *
     * @param event The validation event to validate
     * @throws AdapterValidationException if the event data is invalid
     */
    private void validateEvent(SharedURLValidationCompletedEventDTO event) {
        if (event == null) {
            throw new AdapterValidationException("Validation event cannot be null", "unknown");
        }
        
        if (event.getUrlId() == null || event.getUrlId().trim().isEmpty()) {
            throw new AdapterValidationException("URL ID cannot be null or empty", "unknown");
        }
        
        if (event.getValidationResult() == null) {
            throw new AdapterValidationException("Validation result cannot be null", event.getUrlId());
        }
        
        if (event.getValidationResult().getIsValid() == null) {
            throw new AdapterValidationException("Validation result isValid flag cannot be null", event.getUrlId());
        }
        
        if (event.getValidationResult().getSecurityScore() == null) {
            throw new AdapterValidationException("Security score cannot be null", event.getUrlId());
        }
        
        if (event.getValidationResult().getSecurityScore() < 0 || event.getValidationResult().getSecurityScore() > 100) {
            throw new AdapterValidationException(
                    "Security score must be between 0 and 100, got: " + event.getValidationResult().getSecurityScore(), 
                    event.getUrlId());
        }
        
        if (event.getTimestamp() == null || event.getTimestamp().trim().isEmpty()) {
            throw new AdapterValidationException("Timestamp cannot be null or empty", event.getUrlId());
        }
        
        log.debug("Event validation passed for URL ID: {}", event.getUrlId());
    }
}