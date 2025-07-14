package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainBulkOperationEntity;
import ai.shreds.shared.value_objects.SharedEnumOperationType;

import java.util.List;
import java.util.Map;

/**
 * Port for creating and processing bulk URL operations.
 */
public interface DomainInputPortBulkOperationService {

    /**
     * Initialize a bulk operation.
     * @param owner the user initiating the operation
     * @param operationType the type of bulk operation (CREATE, UPDATE, DELETE)
     * @param urls list of URL data maps to process
     * @return the bulk operation entity
     */
    DomainBulkOperationEntity createBulkOperation(
        String owner,
        SharedEnumOperationType operationType,
        List<Map<String, Object>> urls
    );

    /**
     * Process URL creation within a bulk operation.
     * @param operation the bulk operation context
     * @param urls list of URL data maps to process
     * @return the updated bulk operation entity with results
     */
    DomainBulkOperationEntity processBulkURLCreation(
        DomainBulkOperationEntity operation,
        List<Map<String, Object>> urls
    );
}
