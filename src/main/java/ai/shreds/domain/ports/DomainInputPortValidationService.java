package ai.shreds.domain.ports;

import ai.shreds.shared.dtos.SharedValidationResultDTO;
import ai.shreds.domain.entities.DomainURLValidationResultEntity;

/**
 * Port for processing and retrieving URL validation results.
 */
public interface DomainInputPortValidationService {
    /**
     * Process a completed validation result for a given URL.
     * @param urlId the identifier of the URL
     * @param validationResult the validation result data transfer object
     */
    void processValidationResult(String urlId, SharedValidationResultDTO validationResult);

    /**
     * Retrieve the latest validation result for a given URL.
     * @param urlId the identifier of the URL
     * @return the domain validation result entity
     */
    DomainURLValidationResultEntity getValidationResult(String urlId);
}
