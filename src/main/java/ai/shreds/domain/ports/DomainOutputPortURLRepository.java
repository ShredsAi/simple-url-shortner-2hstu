package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainURLEntity;

/**
 * Output port for URL persistence operations.
 */
public interface DomainOutputPortURLRepository {

    /**
     * Persist a new URL entity.
     */
    DomainURLEntity save(DomainURLEntity url);

    /**
     * Find a URL entity by its identifier.
     */
    DomainURLEntity findById(String urlId);

    /**
     * Update an existing URL entity.
     */
    DomainURLEntity update(DomainURLEntity url);

    /**
     * Delete a URL entity by its identifier.
     */
    void delete(String urlId);

    /**
     * Check existence of a short code in the system.
     */
    boolean existsByShortCode(String shortCode);
}
