package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainURLMetadataEntity;
import ai.shreds.domain.ports.DomainOutputPortURLMetadataRepository;
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
 * MongoDB implementation of the URL metadata repository port.
 * Handles persistence operations for URL metadata entities.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class InfrastructureURLMetadataRepositoryImpl implements DomainOutputPortURLMetadataRepository {
    
    private final MongoTemplate mongoTemplate;
    
    @Override
    public DomainURLMetadataEntity save(DomainURLMetadataEntity metadata) {
        log.debug("Saving URL metadata entity: {}", metadata.getMetadataId());
        
        InfrastructureMongoURLMetadataDocument document = toDocument(metadata);
        if (document.getMetadataId() == null) {
            document.setMetadataId(new ObjectId());
        }
        
        InfrastructureMongoURLMetadataDocument savedDocument = mongoTemplate.save(document);
        DomainURLMetadataEntity savedEntity = toDomainEntity(savedDocument);
        
        log.debug("Successfully saved URL metadata entity: {}", savedEntity.getMetadataId());
        return savedEntity;
    }
    
    @Override
    public DomainURLMetadataEntity findByUrlId(String urlId) {
        log.debug("Finding URL metadata by URL ID: {}", urlId);
        
        try {
            Query query = new Query(Criteria.where("urlId").is(new ObjectId(urlId)));
            InfrastructureMongoURLMetadataDocument document = mongoTemplate.findOne(query, InfrastructureMongoURLMetadataDocument.class);
            
            if (document == null) {
                log.debug("URL metadata not found for URL ID: {}", urlId);
                return null;
            }
            
            DomainURLMetadataEntity entity = toDomainEntity(document);
            log.debug("Found URL metadata entity: {}", entity.getMetadataId());
            return entity;
        } catch (IllegalArgumentException e) {
            log.warn("Invalid ObjectId format: {}", urlId);
            return null;
        }
    }
    
    @Override
    public DomainURLMetadataEntity update(DomainURLMetadataEntity metadata) {
        log.debug("Updating URL metadata entity: {}", metadata.getMetadataId());
        
        try {
            Query query = new Query(Criteria.where("metadataId").is(new ObjectId(metadata.getMetadataId())));
            Update update = new Update()
                    .set("expirationDate", metadata.getExpirationDate())
                    .set("lastAccessedDate", metadata.getLastAccessedDate())
                    .set("accessCount", metadata.getAccessCount())
                    .set("tags", metadata.getTags())
                    .set("qrCodeUrl", metadata.getQrCodeUrl())
                    .set("description", metadata.getDescription())
                    .set("customData", metadata.getCustomData());
            
            mongoTemplate.updateFirst(query, update, InfrastructureMongoURLMetadataDocument.class);
            
            // Find by urlId since that's what we typically search by
            DomainURLMetadataEntity updatedEntity = findByUrlId(metadata.getUrlId());
            log.debug("Successfully updated URL metadata entity: {}", updatedEntity.getMetadataId());
            return updatedEntity;
        } catch (IllegalArgumentException e) {
            log.error("Invalid ObjectId format during update: {}", metadata.getMetadataId());
            throw new RuntimeException("Invalid metadata ID format", e);
        }
    }
    
    /**
     * Converts a MongoDB document to a domain entity.
     */
    private DomainURLMetadataEntity toDomainEntity(InfrastructureMongoURLMetadataDocument document) {
        if (document == null) {
            return null;
        }
        
        return DomainURLMetadataEntity.builder()
                .metadataId(document.getMetadataId().toString())
                .urlId(document.getUrlId().toString())
                .creationDate(document.getCreationDate())
                .expirationDate(document.getExpirationDate())
                .lastAccessedDate(document.getLastAccessedDate())
                .accessCount(document.getAccessCount() != null ? document.getAccessCount() : 0L)
                .tags(document.getTags())
                .qrCodeUrl(document.getQrCodeUrl())
                .description(document.getDescription())
                .customData(document.getCustomData())
                .build();
    }
    
    /**
     * Converts a domain entity to a MongoDB document.
     */
    private InfrastructureMongoURLMetadataDocument toDocument(DomainURLMetadataEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ObjectId metadataId = null;
        if (entity.getMetadataId() != null) {
            try {
                metadataId = new ObjectId(entity.getMetadataId());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid metadata ObjectId format: {}, creating new ObjectId", entity.getMetadataId());
                metadataId = new ObjectId();
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
        
        return InfrastructureMongoURLMetadataDocument.builder()
                .metadataId(metadataId)
                .urlId(urlId)
                .creationDate(entity.getCreationDate() != null ? entity.getCreationDate() : new Date())
                .expirationDate(entity.getExpirationDate())
                .lastAccessedDate(entity.getLastAccessedDate())
                .accessCount(entity.getAccessCount() != null ? entity.getAccessCount() : 0L)
                .tags(entity.getTags())
                .qrCodeUrl(entity.getQrCodeUrl())
                .description(entity.getDescription())
                .customData(entity.getCustomData())
                .build();
    }
}