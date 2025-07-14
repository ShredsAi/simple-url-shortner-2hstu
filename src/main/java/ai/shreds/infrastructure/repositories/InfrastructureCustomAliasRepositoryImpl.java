package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainCustomAliasEntity;
import ai.shreds.domain.ports.DomainOutputPortCustomAliasRepository;
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
 * MongoDB implementation of the custom alias repository port.
 * Handles persistence operations for custom alias entities.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class InfrastructureCustomAliasRepositoryImpl implements DomainOutputPortCustomAliasRepository {
    
    private final MongoTemplate mongoTemplate;
    
    @Override
    public DomainCustomAliasEntity save(DomainCustomAliasEntity alias) {
        log.debug("Saving custom alias entity: {}", alias.getAliasId());
        
        InfrastructureMongoCustomAliasDocument document = toDocument(alias);
        if (document.getAliasId() == null) {
            document.setAliasId(new ObjectId());
        }
        
        InfrastructureMongoCustomAliasDocument savedDocument = mongoTemplate.save(document);
        DomainCustomAliasEntity savedEntity = toDomainEntity(savedDocument);
        
        log.debug("Successfully saved custom alias entity: {}", savedEntity.getAliasId());
        return savedEntity;
    }
    
    @Override
    public DomainCustomAliasEntity findByAliasValue(String aliasValue) {
        log.debug("Finding custom alias by value: {}", aliasValue);
        
        Query query = new Query(Criteria.where("aliasValue").is(aliasValue));
        InfrastructureMongoCustomAliasDocument document = mongoTemplate.findOne(query, InfrastructureMongoCustomAliasDocument.class);
        
        if (document == null) {
            log.debug("Custom alias not found for value: {}", aliasValue);
            return null;
        }
        
        DomainCustomAliasEntity entity = toDomainEntity(document);
        log.debug("Found custom alias entity: {}", entity.getAliasId());
        return entity;
    }
    
    @Override
    public DomainCustomAliasEntity update(DomainCustomAliasEntity alias) {
        log.debug("Updating custom alias entity: {}", alias.getAliasId());
        
        try {
            Query query = new Query(Criteria.where("aliasId").is(new ObjectId(alias.getAliasId())));
            Update update = new Update()
                    .set("isReserved", alias.getIsReserved())
                    .set("owner", alias.getOwner())
                    .set("urlId", alias.getUrlId() != null ? new ObjectId(alias.getUrlId()) : null)
                    .set("expiresAt", alias.getExpiresAt());
            
            mongoTemplate.updateFirst(query, update, InfrastructureMongoCustomAliasDocument.class);
            
            DomainCustomAliasEntity updatedEntity = findByAliasValue(alias.getAliasValue());
            log.debug("Successfully updated custom alias entity: {}", updatedEntity.getAliasId());
            return updatedEntity;
        } catch (IllegalArgumentException e) {
            log.error("Invalid ObjectId format during update: {}", alias.getAliasId());
            throw new RuntimeException("Invalid alias ID format", e);
        }
    }
    
    @Override
    public void delete(String aliasId) {
        log.debug("Deleting custom alias by ID: {}", aliasId);
        
        try {
            Query query = new Query(Criteria.where("aliasId").is(new ObjectId(aliasId)));
            mongoTemplate.remove(query, InfrastructureMongoCustomAliasDocument.class);
            
            log.debug("Successfully deleted custom alias: {}", aliasId);
        } catch (IllegalArgumentException e) {
            log.error("Invalid ObjectId format during deletion: {}", aliasId);
            throw new RuntimeException("Invalid alias ID format", e);
        }
    }
    
    @Override
    public boolean existsByAliasValue(String aliasValue) {
        log.debug("Checking if alias value exists: {}", aliasValue);
        
        Query query = new Query(Criteria.where("aliasValue").is(aliasValue));
        boolean exists = mongoTemplate.exists(query, InfrastructureMongoCustomAliasDocument.class);
        
        log.debug("Alias value {} exists: {}", aliasValue, exists);
        return exists;
    }
    
    /**
     * Converts a MongoDB document to a domain entity.
     */
    private DomainCustomAliasEntity toDomainEntity(InfrastructureMongoCustomAliasDocument document) {
        if (document == null) {
            return null;
        }
        
        return DomainCustomAliasEntity.builder()
                .aliasId(document.getAliasId().toString())
                .aliasValue(document.getAliasValue())
                .isReserved(document.getIsReserved())
                .owner(document.getOwner())
                .urlId(document.getUrlId() != null ? document.getUrlId().toString() : null)
                .createdAt(document.getCreatedAt())
                .expiresAt(document.getExpiresAt())
                .build();
    }
    
    /**
     * Converts a domain entity to a MongoDB document.
     */
    private InfrastructureMongoCustomAliasDocument toDocument(DomainCustomAliasEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ObjectId aliasId = null;
        if (entity.getAliasId() != null) {
            try {
                aliasId = new ObjectId(entity.getAliasId());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid alias ObjectId format: {}, creating new ObjectId", entity.getAliasId());
                aliasId = new ObjectId();
            }
        }
        
        ObjectId urlId = null;
        if (entity.getUrlId() != null) {
            try {
                urlId = new ObjectId(entity.getUrlId());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid URL ObjectId format: {}", entity.getUrlId());
                // Don't throw here, just leave as null
            }
        }
        
        return InfrastructureMongoCustomAliasDocument.builder()
                .aliasId(aliasId)
                .aliasValue(entity.getAliasValue())
                .isReserved(entity.getIsReserved())
                .owner(entity.getOwner())
                .urlId(urlId)
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt() : new Date())
                .expiresAt(entity.getExpiresAt())
                .build();
    }
}