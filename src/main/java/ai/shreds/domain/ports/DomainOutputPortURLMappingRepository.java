package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainURLMappingEntity;

/**
 * Output port for URL mapping persistence operations.
 */
public interface DomainOutputPortURLMappingRepository {

    /**
     * Persist a new URL mapping entity.
     */
    DomainURLMappingEntity save(DomainURLMappingEntity mapping);

    /**
     * Find a URL mapping by its short code.
     */
    DomainURLMappingEntity findByShortCode(String shortCode);

    /**
     * Update an existing URL mapping entity.
     */
    DomainURLMappingEntity update(DomainURLMappingEntity mapping);

    /**
     * Delete a URL mapping entity by its identifier.
     */
    void delete(String mappingId);
}
