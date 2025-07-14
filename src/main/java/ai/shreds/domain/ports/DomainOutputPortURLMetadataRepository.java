package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainURLMetadataEntity;

/**
 * Output port for URL metadata persistence operations.
 */
public interface DomainOutputPortURLMetadataRepository {

    /**
     * Persist a new URL metadata entity.
     */
    DomainURLMetadataEntity save(DomainURLMetadataEntity metadata);

    /**
     * Find URL metadata by URL identifier.
     */
    DomainURLMetadataEntity findByUrlId(String urlId);

    /**
     * Update an existing URL metadata entity.
     */
    DomainURLMetadataEntity update(DomainURLMetadataEntity metadata);
}
