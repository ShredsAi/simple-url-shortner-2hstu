package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainURLEntity;
import ai.shreds.domain.ports.DomainOutputPortURLRepository;
import ai.shreds.shared.value_objects.SharedEnumURLStatus;
import ai.shreds.shared.value_objects.SharedEnumValidationStatus;
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
 * MongoDB implementation of the URL repository port.
 * Handles persistence operations for URL entities.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class InfrastructureURLRepositoryImpl implements DomainOutputPortURLRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public DomainURLEntity save(DomainURLEntity url) {
        log.debug("Saving URL entity: {}", url.getUrlId());

        InfrastructureMongoURLDocument document = toDocument(url);
        if (document.getUrlId() == null) {
            document.setUrlId(new ObjectId());
        }

        InfrastructureMongoURLDocument savedDocument = mongoTemplate.save(document);
        DomainURLEntity savedEntity = toDomainEntity(savedDocument);

        log.debug("Successfully saved URL entity: {}", savedEntity.getUrlId());
        return savedEntity;
    }

    @Override
    public DomainURLEntity findById(String urlId) {
        log.debug("Finding URL by ID: {}", urlId);

        try {
            Query query = new Query(Criteria.where("urlId").is(new ObjectId(urlId)));
            InfrastructureMongoURLDocument document = mongoTemplate.findOne(query, InfrastructureMongoURLDocument.class);

            if (document == null) {
                log.debug("URL not found for ID: {}", urlId);
                return null;
            }

            DomainURLEntity entity = toDomainEntity(document);
            log.debug("Found URL entity: {}", entity.getUrlId());
            return entity;
        } catch (IllegalArgumentException e) {
            log.warn("Invalid ObjectId format: {}", urlId);
            return null;
        }
    }

    @Override
    public DomainURLEntity update(DomainURLEntity url) {
        log.debug("Updating URL entity: {}", url.getUrlId());

        try {
            Query query = new Query(Criteria.where("urlId").is(new ObjectId(url.getUrlId())));
            Update update = new Update()
                    .set("status", url.getStatus().name())
                    .set("updatedAt", new Date())
                    .set("validationStatus", url.getValidationStatus().name())
                    .set("securityScore", url.getSecurityScore());

            if (url.getCustomAlias() != null) {
                update.set("customAlias", url.getCustomAlias());
            }

            mongoTemplate.updateFirst(query, update, InfrastructureMongoURLDocument.class);

            DomainURLEntity updatedEntity = findById(url.getUrlId());
            log.debug("Successfully updated URL entity: {}", updatedEntity.getUrlId());
            return updatedEntity;
        } catch (IllegalArgumentException e) {
            log.error("Invalid ObjectId format during update: {}", url.getUrlId());
            throw new RuntimeException("Invalid URL ID format", e);
        }
    }

    @Override
    public void delete(String urlId) {
        log.debug("Deleting URL by ID: {}", urlId);

        try {
            Query query = new Query(Criteria.where("urlId").is(new ObjectId(urlId)));
            mongoTemplate.remove(query, InfrastructureMongoURLDocument.class);

            log.debug("Successfully deleted URL: {}", urlId);
        } catch (IllegalArgumentException e) {
            log.error("Invalid ObjectId format during deletion: {}", urlId);
            throw new RuntimeException("Invalid URL ID format", e);
        }
    }

    @Override
    public boolean existsByShortCode(String shortCode) {
        log.debug("Checking if short code exists: {}", shortCode);

        Query query = new Query(Criteria.where("shortCode").is(shortCode));
        boolean exists = mongoTemplate.exists(query, InfrastructureMongoURLDocument.class);

        log.debug("Short code {} exists: {}", shortCode, exists);
        return exists;
    }

    /**
     * Converts a MongoDB document to a domain entity.
     */
    private DomainURLEntity toDomainEntity(InfrastructureMongoURLDocument document) {
        if (document == null) {
            return null;
        }

        return DomainURLEntity.builder()
                .urlId(document.getUrlId().toString())
                .originalUrl(document.getOriginalUrl())
                .shortCode(document.getShortCode())
                .status(SharedEnumURLStatus.valueOf(document.getStatus()))
                .owner(document.getOwner())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .validationStatus(SharedEnumValidationStatus.valueOf(document.getValidationStatus()))
                .securityScore(document.getSecurityScore())
                .customAlias(document.getCustomAlias())
                .build();
    }

    /**
     * Converts a domain entity to a MongoDB document.
     */
    private InfrastructureMongoURLDocument toDocument(DomainURLEntity entity) {
        if (entity == null) {
            return null;
        }

        ObjectId objectId = null;
        if (entity.getUrlId() != null) {
            try {
                objectId = new ObjectId(entity.getUrlId());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid ObjectId format: {}, creating new ObjectId", entity.getUrlId());
                objectId = new ObjectId();
            }
        }

        return InfrastructureMongoURLDocument.builder()
                .urlId(objectId)
                .originalUrl(entity.getOriginalUrl())
                .shortCode(entity.getShortCode())
                .status(entity.getStatus() != null ? entity.getStatus().name() : SharedEnumURLStatus.ACTIVE.name())
                .owner(entity.getOwner())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt() : new Date())
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt() : new Date())
                .validationStatus(entity.getValidationStatus() != null ? entity.getValidationStatus().name() : SharedEnumValidationStatus.PENDING.name())
                .securityScore(entity.getSecurityScore())
                .customAlias(entity.getCustomAlias())
                .build();
    }
}