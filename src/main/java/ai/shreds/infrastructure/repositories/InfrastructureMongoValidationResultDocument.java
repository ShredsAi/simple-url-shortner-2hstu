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
 * MongoDB document representing URL validation results.
 * Contains security assessment and threat analysis for URLs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "url_validation_result")
public class InfrastructureMongoValidationResultDocument {
    
    @Id
    private ObjectId validationId;
    
    @Field("urlId")
    @Indexed(unique = true)
    private ObjectId urlId;
    
    @Field("isValid")
    private Boolean isValid;
    
    @Field("securityScore")
    private Integer securityScore;
    
    @Field("threats")
    private List<String> threats;
    
    @Field("categories")
    private List<String> categories;
    
    @Field("validationDate")
    private Date validationDate;
    
    @Field("validationSource")
    private String validationSource;
    
    @Field("validationDetails")
    private Map<String, Object> validationDetails;
}