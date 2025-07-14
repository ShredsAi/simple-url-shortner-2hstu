package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainCustomAliasEntity;

/**
 * Output port for custom alias persistence operations.
 */
public interface DomainOutputPortCustomAliasRepository {

    /**
     * Persist a new custom alias entity.
     */
    DomainCustomAliasEntity save(DomainCustomAliasEntity alias);

    /**
     * Find a custom alias by its value.
     */
    DomainCustomAliasEntity findByAliasValue(String aliasValue);

    /**
     * Update an existing custom alias entity.
     */
    DomainCustomAliasEntity update(DomainCustomAliasEntity alias);

    /**
     * Delete a custom alias entity by its identifier.
     */
    void delete(String aliasId);

    /**
     * Check if a specific alias value already exists.
     */
    boolean existsByAliasValue(String aliasValue);
}
