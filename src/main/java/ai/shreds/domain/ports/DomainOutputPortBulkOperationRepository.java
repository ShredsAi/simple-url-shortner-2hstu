package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainBulkOperationEntity;

/**
 * Output port for bulk operation persistence operations.
 */
public interface DomainOutputPortBulkOperationRepository {

    /**
     * Persist a new bulk operation entity.
     */
    DomainBulkOperationEntity save(DomainBulkOperationEntity operation);

    /**
     * Find a bulk operation by its identifier.
     */
    DomainBulkOperationEntity findById(String operationId);

    /**
     * Update an existing bulk operation entity.
     */
    DomainBulkOperationEntity update(DomainBulkOperationEntity operation);
}
