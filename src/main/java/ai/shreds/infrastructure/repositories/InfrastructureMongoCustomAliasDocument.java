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
 * MongoDB document representing a custom alias reservation.
 * Used for managing user-defined URL aliases.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "custom_alias")
public class InfrastructureMongoCustomAliasDocument {
    
    @Id
    private ObjectId aliasId;
    
    @Field("aliasValue")
    @Indexed(unique = true)
    private String aliasValue;
    
    @Field("isReserved")
    private Boolean isReserved;
    
    @Field("owner")
    @Indexed
    private String owner;
    
    @Field("urlId")
    @Indexed
    private ObjectId urlId;
    
    @Field("createdAt")
    private Date createdAt;
    
    @Field("expiresAt")
    @Indexed
    private Date expiresAt;
}