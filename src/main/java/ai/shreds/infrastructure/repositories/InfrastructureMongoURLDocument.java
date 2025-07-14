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
 * MongoDB document representing a URL entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "url")
public class InfrastructureMongoURLDocument {
    
    @Id
    private ObjectId urlId;
    
    @Field("originalUrl")
    @Indexed
    private String originalUrl;
    
    @Field("shortCode")
    @Indexed(unique = true)
    private String shortCode;
    
    @Field("status")
    private String status;
    
    @Field("owner")
    @Indexed
    private String owner;
    
    @Field("createdAt")
    private Date createdAt;
    
    @Field("updatedAt")
    private Date updatedAt;
    
    @Field("validationStatus")
    private String validationStatus;
    
    @Field("securityScore")
    private Integer securityScore;
    
    @Field("customAlias")
    private String customAlias;
}