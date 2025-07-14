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
 * MongoDB document representing a bulk operation on multiple URLs.
 * Tracks the progress and results of batch URL operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bulk_operation")
public class InfrastructureMongoBulkOperationDocument {
    
    @Id
    private ObjectId operationId;
    
    @Field("owner")
    @Indexed
    private String owner;
    
    @Field("operationType")
    private String operationType;
    
    @Field("totalRequested")
    private Integer totalRequested;
    
    @Field("successful")
    private Integer successful;
    
    @Field("failed")
    private Integer failed;
    
    @Field("startTime")
    @Indexed
    private Date startTime;
    
    @Field("endTime")
    private Date endTime;
    
    @Field("status")
    @Indexed
    private String status;
    
    @Field("resultUrls")
    private List<String> resultUrls;
    
    @Field("errorDetails")
    private List<Map<String, String>> errorDetails;
}