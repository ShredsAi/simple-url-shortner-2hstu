package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainURLMappingEntity;
import ai.shreds.domain.ports.DomainOutputPortURLMappingRepository;
import ai.shreds.shared.value_objects.SharedEnumURLStatus;
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
 * MongoDB implementation of the URL mapping repository port.
 * Handles persistence operations for URL mapping entities.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class InfrastructureURLMappingRepositoryImpl implements DomainOutputPortURLMappingRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public DomainURLMappingEntity save(DomainURLMappingEntity mapping) {
        log.debug("Saving URL mapping entity: {}", mapping.getMappingId());

        InfrastructureMongoURLMappingDocument document = toDocument(mapping);
        if (document.getMappingId() == null) {
            document.setMappingId(new ObjectId());
        }

        InfrastructureMongoURLMappingDocument savedDocument = mongoTemplate.save(document);
        DomainURLMappingEntity savedEntity = toDomainEntity(savedDocument);

        log.debug("Successfully saved URL mapping entity: {}", savedEntity.getMappingId());
        return savedEntity;
    }

    @Override
    public DomainURLMappingEntity findByShortCode(String shortCode) {
        log.debug("Finding URL mapping by short code: {}", shortCode);

        Query query = new Query(Criteria.where("shortCode").is(shortCode));
        InfrastructureMongoURLMappingDocument document = mongoTemplate.findOne(query, InfrastructureMongoURLMappingDocument.class);

        if (document == null) {
            log.debug("URL mapping not found for short code: {}", shortCode);
            return null;
        }

        DomainURLMappingEntity entity = toDomainEntity(document);
        log.debug("Found URL mapping entity: {}", entity.getMappingId());
        return entity;
    }

    @Override
    public DomainURLMappingEntity update(DomainURLMappingEntity mapping) {
        log.debug("Updating URL mapping entity: {}", mapping.getMappingId());

        try {
            Query query = new Query(Criteria.where("mappingId").is(new ObjectId(mapping.getMappingId())));
            Update update = new Update()
                    .set("status", mapping.getStatus().name())
                    .set("originalUrl", mapping.getOriginalUrl());

            mongoTemplate.updateFirst(query, update, InfrastructureMongoURLMappingDocument.class);

            DomainURLMappingEntity updatedEntity = findByShortCode(mapping.getShortCode());
            log.debug("Successfully updated URL mapping entity: {}", updatedEntity.getMappingId());
            return updatedEntity;
        } catch (IllegalArgumentException e) {
            log.error("Invalid ObjectId format during update: {}", mapping.getMappingId());
            throw new RuntimeException("Invalid mapping ID format", e);
        }
    }

    @Override
    public void delete(String mappingId) {
        log.debug("Deleting URL mapping by ID: {}", mappingId);

        try {
            Query query = new Query(Criteria.where("mappingId").is(new ObjectId(mappingId)));
            mongoTemplate.remove(query, InfrastructureMongoURLMappingDocument.class);

            log.debug("Successfully deleted URL mapping: {}", mappingId);
        } catch (IllegalArgumentException e) {
            log.error("Invalid ObjectId format during deletion: {}", mappingId);
            throw new RuntimeException("Invalid mapping ID format", e);
        }
    }

    /**
     * Converts a MongoDB document to a domain entity.
     */
    private DomainURLMappingEntity toDomainEntity(InfrastructureMongoURLMappingDocument document) {
        if (document == null) {
            return null;
        }

        return DomainURLMappingEntity.builder()
                .mappingId(document.getMappingId().toString())
                .shortCode(document.getShortCode())
                .originalUrl(document.getOriginalUrl())
                .urlId(document.getUrlId().toString())
                .status(SharedEnumURLStatus.valueOf(document.getStatus()))
                .createdAt(document.getCreatedAt())
                .build();
    }

    /**
     * Converts a domain entity to a MongoDB document.
     */
    private InfrastructureMongoURLMappingDocument toDocument(DomainURLMappingEntity entity) {
        if (entity == null) {
            return null;
        }

        ObjectId mappingId = null;
        if (entity.getMappingId() != null) {
            try {
                mappingId = new ObjectId(entity.getMappingId());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid mapping ObjectId format: {}, creating new ObjectId", entity.getMappingId());
                mappingId = new ObjectId();
            }
        }

        ObjectId urlId = null;
        if (entity.getUrlId() != null) {
            try {
                urlId = new ObjectId(entity.getUrlId());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid URL ObjectId format: {}", entity.getUrlId());
                throw new RuntimeException("Invalid URL ID format", e);
            }
        }

        return InfrastructureMongoURLMappingDocument.builder()
                .mappingId(mappingId)
                .shortCode(entity.getShortCode())
                .originalUrl(entity.getOriginalUrl())
                .urlId(urlId)
                .status(entity.getStatus() != null ? entity.getStatus().name() : SharedEnumURLStatus.ACTIVE.name())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt() : new Date())
                .build();
    }
}