package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedURLValidationCompletedEventDTO;

/**
 * Input port for processing URL validation results.
 * This interface defines the contract for handling validation completion events.
 */
public interface ApplicationInputPortProcessValidationResult {

    /**
     * Processes a validation result when URL validation is completed.
     *
     * @param event the validation completed event containing URL ID, validation result, and timestamp
     */
    void processValidationResult(SharedURLValidationCompletedEventDTO event);
}