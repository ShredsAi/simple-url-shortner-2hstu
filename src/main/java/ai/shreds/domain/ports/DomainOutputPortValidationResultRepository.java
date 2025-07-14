package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainURLValidationResultEntity;

/**
 * Output port for URL validation result persistence operations.
 */
public interface DomainOutputPortValidationResultRepository {

    /**
     * Persist a new validation result entity.
     */
    DomainURLValidationResultEntity save(DomainURLValidationResultEntity result);

    /**
     * Find validation result by URL identifier.
     */
    DomainURLValidationResultEntity findByUrlId(String urlId);

    /**
     * Update an existing validation result entity.
     */
    DomainURLValidationResultEntity update(DomainURLValidationResultEntity result);
}
