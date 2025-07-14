package ai.shreds.infrastructure.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

@Configuration
public class InfrastructureMongoDBConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database:#{null}}")
    private String databaseName;

    @Value("${spring.data.mongodb.connection-pool-size:50}")
    private int connectionPoolSize;

    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient client) {
        String db = databaseName != null ? databaseName : connectionStringDatabase();
        return new MongoTemplate(client, db);
    }

    private String connectionStringDatabase() {
        int idx = mongoUri.lastIndexOf('/');
        return idx != -1 ? mongoUri.substring(idx + 1) : "";
    }

    @PostConstruct
    public void configureIndexes() {
        // url collection
        IndexOperations urlOps = mongoTemplate(mongoClient()).indexOps("url");
        urlOps.ensureIndex(new Index().on("originalUrl", Sort.Direction.ASC));
        urlOps.ensureIndex(new Index().on("shortCode", Sort.Direction.ASC).unique());
        urlOps.ensureIndex(new Index().on("owner", Sort.Direction.ASC));
        urlOps.ensureIndex(new Index().on("status", Sort.Direction.ASC));

        // url_mapping collection
        IndexOperations mappingOps = mongoTemplate(mongoClient()).indexOps("url_mapping");
        mappingOps.ensureIndex(new Index().on("shortCode", Sort.Direction.ASC).unique());
        mappingOps.ensureIndex(new Index().on("urlId", Sort.Direction.ASC));

        // custom_alias collection
        IndexOperations aliasOps = mongoTemplate(mongoClient()).indexOps("custom_alias");
        aliasOps.ensureIndex(new Index().on("aliasValue", Sort.Direction.ASC).unique());
        aliasOps.ensureIndex(new Index().on("owner", Sort.Direction.ASC));
        aliasOps.ensureIndex(new Index().on("expiresAt", Sort.Direction.ASC).expire(0L));

        // url_metadata collection
        IndexOperations metadataOps = mongoTemplate(mongoClient()).indexOps("url_metadata");
        metadataOps.ensureIndex(new Index().on("urlId", Sort.Direction.ASC).unique());
        metadataOps.ensureIndex(new Index().on("expirationDate", Sort.Direction.ASC).expire(0L));

        // url_validation_result collection
        IndexOperations validationOps = mongoTemplate(mongoClient()).indexOps("url_validation_result");
        validationOps.ensureIndex(new Index().on("urlId", Sort.Direction.ASC).unique());

        // bulk_operation collection
        IndexOperations bulkOps = mongoTemplate(mongoClient()).indexOps("bulk_operation");
        bulkOps.ensureIndex(new Index().on("owner", Sort.Direction.ASC));
        bulkOps.ensureIndex(new Index().on("status", Sort.Direction.ASC));
        bulkOps.ensureIndex(new Index().on("startTime", Sort.Direction.ASC));
    }
}
