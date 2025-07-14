package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainURLEntity;
import ai.shreds.domain.entities.DomainURLValidationResultEntity;
import ai.shreds.domain.exceptions.DomainException;
import ai.shreds.domain.ports.DomainInputPortValidationService;
import ai.shreds.domain.ports.DomainOutputPortURLRepository;
import ai.shreds.domain.ports.DomainOutputPortValidationResultRepository;
import ai.shreds.shared.dtos.SharedValidationResultDTO;
import ai.shreds.shared.value_objects.SharedEnumURLStatus;
import ai.shreds.shared.value_objects.SharedEnumValidationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Domain service for coordinating URL validation.
 * Processes validation results and manages URL states based on security assessments.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DomainValidationCoordinationService implements DomainInputPortValidationService {

    private static final int SECURITY_THRESHOLD = 70;
    
    private final DomainOutputPortValidationResultRepository validationRepository;
    private final DomainOutputPortURLRepository urlRepository;
    
    @Override
    public void processValidationResult(String urlId, SharedValidationResultDTO validationResult) {
        log.info("Processing validation result for URL ID: {}", urlId);
        
        if (urlId == null || urlId.trim().isEmpty()) {
            throw new DomainException("URL ID cannot be null or empty");
        }
        
        if (validationResult == null) {
            throw new DomainException("Validation result cannot be null");
        }
        
        try {
            // Retrieve URL entity
            DomainURLEntity url = urlRepository.findById(urlId);
            if (url == null) {
                throw new DomainException("URL not found with ID: " + urlId);
            }
            
            // Determine validation status
            SharedEnumValidationStatus validationStatus;
            if (validationResult.getIsValid() && 
                (validationResult.getSecurityScore() == null || validationResult.getSecurityScore() >= SECURITY_THRESHOLD)) {
                validationStatus = SharedEnumValidationStatus.VALIDATED;
            } else {
                validationStatus = SharedEnumValidationStatus.REJECTED;
                // Update URL status to deactivated if it fails validation
                url.updateStatus(SharedEnumURLStatus.DEACTIVATED);
                urlRepository.update(url);
            }
            
            // Create and save validation result entity
            DomainURLValidationResultEntity validationEntity = DomainURLValidationResultEntity.builder()
                    .validationId(UUID.randomUUID().toString())
                    .urlId(urlId)
                    .isValid(validationResult.getIsValid())
                    .securityScore(validationResult.getSecurityScore())
                    .threats(validationResult.getThreats() != null ? 
                            Arrays.asList(validationResult.getThreats()) : null)
                    .categories(validationResult.getCategories() != null ? 
                            Arrays.asList(validationResult.getCategories()) : null)
                    .validationDate(new Date())
                    .validationSource("external-validation-service")
                    .validationDetails(null) // Detailed results not included in DTO
                    .build();
            
            validationRepository.save(validationEntity);
            
            // Update URL validation state
            url.updateValidationState(validationStatus, validationResult.getSecurityScore());
            urlRepository.update(url);
            
            log.info("Validation processed for URL ID: {}, status: {}, score: {}", 
                    urlId, validationStatus, validationResult.getSecurityScore());
                    
        } catch (Exception e) {
            log.error("Error processing validation result: {}", e.getMessage(), e);
            throw new DomainException("Failed to process validation result: " + e.getMessage(), e);
        }
    }
    
    @Override
    public DomainURLValidationResultEntity getValidationResult(String urlId) {
        log.info("Retrieving validation result for URL ID: {}", urlId);
        
        if (urlId == null || urlId.trim().isEmpty()) {
            throw new DomainException("URL ID cannot be null or empty");
        }
        
        DomainURLValidationResultEntity result = validationRepository.findByUrlId(urlId);
        if (result == null) {
            log.warn("No validation result found for URL ID: {}", urlId);
            throw new DomainException("Validation result not found for URL ID: " + urlId);
        }
        
        return result;
    }
    
    /**
     * Updates the URL validation status directly.
     * 
     * @param urlId The URL ID to update
     * @param status The new validation status to set
     */
    private void updateURLValidationStatus(String urlId, SharedEnumValidationStatus status) {
        DomainURLEntity url = urlRepository.findById(urlId);
        if (url != null) {
            url.setValidationStatus(status);
            url.setUpdatedAt(new Date());
            urlRepository.update(url);
        }
    }
}