package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainURLValidationResultEntity;
import ai.shreds.domain.ports.DomainOutputPortValidationResultRepository;
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
 * MongoDB implementation of the URL validation result repository port.
 * Handles persistence operations for URL validation result entities.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class InfrastructureValidationResultRepositoryImpl implements DomainOutputPortValidationResultRepository {
    
    private final MongoTemplate mongoTemplate;
    
    @Override
    public DomainURLValidationResultEntity save(DomainURLValidationResultEntity result) {
        log.debug("Saving URL validation result entity: {}", result.getValidationId());
        
        InfrastructureMongoValidationResultDocument document = toDocument(result);
        if (document.getValidationId() == null) {
            document.setValidationId(new ObjectId());
        }
        
        InfrastructureMongoValidationResultDocument savedDocument = mongoTemplate.save(document);
        DomainURLValidationResultEntity savedEntity = toDomainEntity(savedDocument);
        
        log.debug("Successfully saved URL validation result entity: {}", savedEntity.getValidationId());
        return savedEntity;
    }
    
    @Override
    public DomainURLValidationResultEntity findByUrlId(String urlId) {
        log.debug("Finding URL validation result by URL ID: {}", urlId);
        
        try {
            Query query = new Query(Criteria.where("urlId").is(new ObjectId(urlId)));
            InfrastructureMongoValidationResultDocument document = mongoTemplate.findOne(query, InfrastructureMongoValidationResultDocument.class);
            
            if (document == null) {
                log.debug("URL validation result not found for URL ID: {}", urlId);
                return null;
            }
            
            DomainURLValidationResultEntity entity = toDomainEntity(document);
            log.debug("Found URL validation result entity: {}", entity.getValidationId());
            return entity;
        } catch (IllegalArgumentException e) {
            log.warn("Invalid ObjectId format: {}", urlId);
            return null;
        }
    }
    
    @Override
    public DomainURLValidationResultEntity update(DomainURLValidationResultEntity result) {
        log.debug("Updating URL validation result entity: {}", result.getValidationId());
        
        try {
            Query query = new Query(Criteria.where("validationId").is(new ObjectId(result.getValidationId())));
            Update update = new Update()
                    .set("isValid", result.getIsValid())
                    .set("securityScore", result.getSecurityScore())
                    .set("threats", result.getThreats())
                    .set("categories", result.getCategories())
                    .set("validationDate", result.getValidationDate())
                    .set("validationSource", result.getValidationSource())
                    .set("validationDetails", result.getValidationDetails());
            
            mongoTemplate.updateFirst(query, update, InfrastructureMongoValidationResultDocument.class);
            
            // Find by urlId since that's what we typically search by
            DomainURLValidationResultEntity updatedEntity = findByUrlId(result.getUrlId());
            log.debug("Successfully updated URL validation result entity: {}", updatedEntity.getValidationId());
            return updatedEntity;
        } catch (IllegalArgumentException e) {
            log.error("Invalid ObjectId format during update: {}", result.getValidationId());
            throw new RuntimeException("Invalid validation ID format", e);
        }
    }
    
    /**
     * Converts a MongoDB document to a domain entity.
     */
    private DomainURLValidationResultEntity toDomainEntity(InfrastructureMongoValidationResultDocument document) {
        if (document == null) {
            return null;
        }
        
        return DomainURLValidationResultEntity.builder()
                .validationId(document.getValidationId().toString())
                .urlId(document.getUrlId().toString())
                .isValid(document.getIsValid())
                .securityScore(document.getSecurityScore())
                .threats(document.getThreats())
                .categories(document.getCategories())
                .validationDate(document.getValidationDate())
                .validationSource(document.getValidationSource())
                .validationDetails(document.getValidationDetails())
                .build();
    }
    
    /**
     * Converts a domain entity to a MongoDB document.
     */
    private InfrastructureMongoValidationResultDocument toDocument(DomainURLValidationResultEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ObjectId validationId = null;
        if (entity.getValidationId() != null) {
            try {
                validationId = new ObjectId(entity.getValidationId());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid validation ObjectId format: {}, creating new ObjectId", entity.getValidationId());
                validationId = new ObjectId();
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
        
        return InfrastructureMongoValidationResultDocument.builder()
                .validationId(validationId)
                .urlId(urlId)
                .isValid(entity.getIsValid())
                .securityScore(entity.getSecurityScore())
                .threats(entity.getThreats())
                .categories(entity.getCategories())
                .validationDate(entity.getValidationDate() != null ? entity.getValidationDate() : new Date())
                .validationSource(entity.getValidationSource())
                .validationDetails(entity.getValidationDetails())
                .build();
    }
}