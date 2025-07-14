package ai.shreds.infrastructure.repositories;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * MongoDB document representing a URL mapping for fast lookup.
 * Maps short codes to original URLs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "url_mapping")
public class InfrastructureMongoURLMappingDocument {
    
    @Id
    private ObjectId mappingId;
    
    @Field("shortCode")
    @Indexed(unique = true)
    private String shortCode;
    
    @Field("originalUrl")
    private String originalUrl;
    
    @Field("urlId")
    @Indexed
    private ObjectId urlId;
    
    @Field("status")
    private String status;
    
    @Field("createdAt")
    private Date createdAt;
}