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
import java.util.List;
import java.util.Map;

/**
 * MongoDB document representing URL metadata.
 * Contains additional information about URLs such as creation date,
 * expiration, access statistics, and custom metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "url_metadata")
public class InfrastructureMongoURLMetadataDocument {
    
    @Id
    private ObjectId metadataId;
    
    @Field("urlId")
    @Indexed(unique = true)
    private ObjectId urlId;
    
    @Field("creationDate")
    private Date creationDate;
    
    @Field("expirationDate")
    @Indexed
    private Date expirationDate;
    
    @Field("lastAccessedDate")
    private Date lastAccessedDate;
    
    @Field("accessCount")
    private Long accessCount;
    
    @Field("tags")
    @Indexed
    private List<String> tags;
    
    @Field("qrCodeUrl")
    private String qrCodeUrl;
    
    @Field("description")
    private String description;
    
    @Field("customData")
    private Map<String, Object> customData;
}