package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainBulkOperationEntity;
import ai.shreds.domain.ports.DomainOutputPortBulkOperationRepository;
import ai.shreds.shared.value_objects.SharedEnumOperationStatus;
import ai.shreds.shared.value_objects.SharedEnumOperationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * MongoDB implementation of the bulk operation repository port.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class InfrastructureBulkOperationRepositoryImpl implements DomainOutputPortBulkOperationRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public DomainBulkOperationEntity save(DomainBulkOperationEntity operation) {
        log.debug("Saving bulk operation entity: {}", operation.getOperationId());
        InfrastructureMongoBulkOperationDocument document = toDocument(operation);
        if (document.getOperationId() == null) {
            document.setOperationId(new ObjectId());
        }
        InfrastructureMongoBulkOperationDocument saved = mongoTemplate.save(document);
        DomainBulkOperationEntity savedEntity = toDomainEntity(saved);
        log.debug("Successfully saved bulk operation: {}", savedEntity.getOperationId());
        return savedEntity;
    }

    @Override
    public DomainBulkOperationEntity findById(String operationId) {
        log.debug("Finding bulk operation by ID: {}", operationId);
        try {
            Query query = new Query(Criteria.where("operationId").is(new ObjectId(operationId)));
            InfrastructureMongoBulkOperationDocument doc = mongoTemplate.findOne(query, InfrastructureMongoBulkOperationDocument.class);
            if (doc == null) {
                log.debug("Bulk operation not found for ID: {}", operationId);
                return null;
            }
            return toDomainEntity(doc);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid ObjectId format for bulk operation ID: {}", operationId);
            return null;
        }
    }

    @Override
    public DomainBulkOperationEntity update(DomainBulkOperationEntity operation) {
        log.debug("Updating bulk operation entity: {}", operation.getOperationId());
        try {
            Query query = new Query(Criteria.where("operationId").is(new ObjectId(operation.getOperationId())));
            Update update = new Update()
                .set("owner", operation.getOwner())
                .set("operationType", operation.getOperationType().name())
                .set("totalRequested", operation.getTotalRequested())
                .set("successful", operation.getSuccessful())
                .set("failed", operation.getFailed())
                .set("startTime", operation.getStartTime())
                .set("endTime", operation.getEndTime())
                .set("status", operation.getStatus().name())
                .set("resultUrls", operation.getResultUrls())
                .set("errorDetails", operation.getErrorDetails());
            mongoTemplate.updateFirst(query, update, InfrastructureMongoBulkOperationDocument.class);
            // Return the updated document
            return findById(operation.getOperationId());
        } catch (IllegalArgumentException e) {
            log.error("Invalid ObjectId format during bulk operation update: {}", operation.getOperationId(), e);
            throw new RuntimeException("Invalid bulk operation ID format", e);
        }
    }

    private DomainBulkOperationEntity toDomainEntity(InfrastructureMongoBulkOperationDocument doc) {
        if (doc == null) {
            return null;
        }
        return DomainBulkOperationEntity.builder()
                .operationId(doc.getOperationId().toString())
                .owner(doc.getOwner())
                .operationType(SharedEnumOperationType.valueOf(doc.getOperationType()))
                .totalRequested(doc.getTotalRequested())
                .successful(doc.getSuccessful())
                .failed(doc.getFailed())
                .startTime(doc.getStartTime())
                .endTime(doc.getEndTime())
                .status(SharedEnumOperationStatus.valueOf(doc.getStatus()))
                .resultUrls(doc.getResultUrls())
                .errorDetails(doc.getErrorDetails())
                .build();
    }

    private InfrastructureMongoBulkOperationDocument toDocument(DomainBulkOperationEntity entity) {
        if (entity == null) {
            return null;
        }
        ObjectId id = null;
        if (entity.getOperationId() != null) {
            try {
                id = new ObjectId(entity.getOperationId());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid bulk operation ObjectId format: {}, generating new ID", entity.getOperationId());
                id = new ObjectId();
            }
        }
        return InfrastructureMongoBulkOperationDocument.builder()
                .operationId(id)
                .owner(entity.getOwner())
                .operationType(entity.getOperationType().name())
                .totalRequested(entity.getTotalRequested())
                .successful(entity.getSuccessful())
                .failed(entity.getFailed())
                .startTime(entity.getStartTime() != null ? entity.getStartTime() : new Date())
                .endTime(entity.getEndTime())
                .status(entity.getStatus().name())
                .resultUrls(entity.getResultUrls())
                .errorDetails(entity.getErrorDetails())
                .build();
    }
}