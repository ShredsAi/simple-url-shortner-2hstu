package ai.shreds.application.services;

import ai.shreds.application.ports.ApplicationInputPortProcessValidationResult;
import ai.shreds.domain.ports.DomainInputPortValidationService;
import ai.shreds.shared.dtos.SharedURLValidationCompletedEventDTO;
import ai.shreds.shared.dtos.SharedValidationResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationURLValidationService implements ApplicationInputPortProcessValidationResult {

    private final DomainInputPortValidationService domainValidationService;
    private final ApplicationEventPublishingService eventPublishingService;

    @Override
    @Transactional
    public void processValidationResult(SharedURLValidationCompletedEventDTO event) {
        log.info("Processing validation result for URL ID: {}", event.getUrlId());
        
        SharedValidationResultDTO validationResult = event.getValidationResult();
        String urlId = event.getUrlId();
        
        try {
            // Update the URL's validation status in the domain layer
            domainValidationService.processValidationResult(urlId, validationResult);
            
            // Log the validation outcome
            if (validationResult.getIsValid()) {
                log.info("URL ID {} passed validation with security score: {}", 
                         urlId, validationResult.getSecurityScore());
            } else {
                log.warn("URL ID {} failed validation. Security score: {}, Threats: {}", 
                         urlId, validationResult.getSecurityScore(), 
                         String.join(", ", validationResult.getThreats()));
                
                // For rejected URLs, we might want to publish a separate event
                if (validationResult.getSecurityScore() < getMinimumSecurityThreshold()) {
                    // This could trigger notifications or other actions for dangerous URLs
                    eventPublishingService.notifyURLRejected(urlId, validationResult);
                }
            }
        } catch (Exception e) {
            log.error("Failed to process validation result for URL ID: {}", urlId, e);
            // Could implement retry logic or dead-letter queue handling here
        }
    }
    
    /**
     * Get the minimum security threshold for URL acceptance.
     * This could be externalized to configuration.
     */
    private int getMinimumSecurityThreshold() {
        return 50; // Default minimum security score
    }
}
