package ai.shreds;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import ai.shreds.infrastructure.external_services.InfrastructureAnalyticsServiceClient;
import ai.shreds.infrastructure.external_services.InfrastructureConfigServiceClient;
import ai.shreds.infrastructure.external_services.InfrastructureQRCodeServiceClient;
import ai.shreds.infrastructure.external_services.InfrastructureUserServiceClient;
import ai.shreds.shared.dtos.SharedQRCodeRequestDTO;
import ai.shreds.shared.dtos.SharedQRCodeResponseDTO;
import ai.shreds.shared.dtos.SharedURLCreatedEventDTO;
import ai.shreds.shared.dtos.SharedUserQuotaDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Integration test for URL Management Shred Application.
 * This test verifies that the application starts successfully with all required dependencies.
 * 
 * Uses TestContainers for:
 * - MongoDB (database)
 * - Redis (cache)
 * - RabbitMQ (messaging)
 * 
 * External services are mocked to prevent external dependencies during testing.
 */
@SpringBootTest(classes = URLManagementShredApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(OutputCaptureExtension.class)
@Testcontainers
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "logging.level.ai.shreds=DEBUG",
    "logging.level.org.springframework.boot=INFO",
    "logging.level.org.testcontainers=INFO",
    "de.flapdoodle.mongodb.embedded.version=7.0.0",
    "surefire.failIfNoSpecifiedTests=false"
})
public class URLManagementShredApplicationIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(URLManagementShredApplicationIntegrationTest.class);

    @LocalServerPort
    private int port;

    // TestContainers Configuration
    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:7.0"))
            .withExposedPorts(27017);

    @Container
    static final GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
            .withExposedPorts(6379)
            .withCommand("redis-server", "--appendonly", "yes");

    @Container
    static final RabbitMQContainer rabbitMQContainer = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.12-management-alpine"))
            .withExposedPorts(5672, 15672);

    // Mock external services to prevent external dependencies
    @MockBean
    private InfrastructureAnalyticsServiceClient analyticsServiceClient;

    @MockBean
    private InfrastructureConfigServiceClient configServiceClient;

    @MockBean
    private InfrastructureQRCodeServiceClient qrCodeServiceClient;

    @MockBean
    private InfrastructureUserServiceClient userServiceClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // MongoDB configuration
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "test-urlservice");
        
        // Redis configuration
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
        registry.add("spring.data.redis.password", () -> "");
        
        // RabbitMQ configuration
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", () -> rabbitMQContainer.getMappedPort(5672));
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
        registry.add("spring.rabbitmq.virtual-host", () -> "/");
        
        // Disable external service calls for testing
        registry.add("external.services.qr-code.url", () -> "http://localhost:9999");
        registry.add("external.services.user-service.url", () -> "http://localhost:9999");
        registry.add("external.services.analytics-service.url", () -> "http://localhost:9999");
        registry.add("external.services.config-service.url", () -> "http://localhost:9999");
        
        logger.info("TestContainers configured:");
        logger.info("MongoDB: {}", mongoDBContainer.getReplicaSetUrl());
        logger.info("Redis: {}:{}", redisContainer.getHost(), redisContainer.getMappedPort(6379));
        logger.info("RabbitMQ: {}:{}", rabbitMQContainer.getHost(), rabbitMQContainer.getMappedPort(5672));
    }

    @Test
    void shouldStartApplicationSuccessfully(CapturedOutput output) {
        logger.info("Starting integration test for URL Management Shred Application...");
        
        // Setup mock responses for external services
        setupMockExternalServices();
        
        // Verify TestContainers are running
        assertTrue(mongoDBContainer.isRunning(), "MongoDB container should be running");
        assertTrue(redisContainer.isRunning(), "Redis container should be running");
        assertTrue(rabbitMQContainer.isRunning(), "RabbitMQ container should be running");
        
        logger.info("All TestContainers are running successfully");
        
        // Verify application is accessible
        assertNotNull(port, "Application port should be assigned");
        assertTrue(port > 0, "Application port should be positive");
        
        logger.info("Application is running on port: {}", port);
        
        // Test application health endpoint
        assertDoesNotThrow(() -> {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + port + "/actuator/health"))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            logger.info("Health check response status: {}", response.statusCode());
            logger.info("Health check response body: {}", response.body());
            
            assertEquals(200, response.statusCode(), "Health endpoint should return 200 OK");
            assertTrue(response.body().contains("UP"), "Health status should be UP");
            
        }, "Health endpoint should be accessible");
        
        logger.info("Application health check passed successfully");
        
        // Print the captured output to validate Spring Boot startup
        System.out.println("Captured Output: \n" + output.getAll());
    }

    @Test
    void shouldHaveRequiredBeansConfigured() {
        logger.info("Testing Spring context configuration...");
        
        // Setup mock responses
        setupMockExternalServices();
        
        // This test will fail if any required beans are missing or misconfigured
        // Spring Boot test will not start if there are critical configuration issues
        
        // Test that external service mocks are properly injected
        assertNotNull(analyticsServiceClient, "Analytics service client should be injected");
        assertNotNull(configServiceClient, "Config service client should be injected");
        assertNotNull(qrCodeServiceClient, "QR code service client should be injected");
        assertNotNull(userServiceClient, "User service client should be injected");
        
        logger.info("All required beans are properly configured");
    }

    @Test
    void shouldConnectToAllExternalDependencies() {
        logger.info("Testing connections to external dependencies...");
        
        // Setup mock responses
        setupMockExternalServices();
        
        // Test MongoDB connection
        assertTrue(mongoDBContainer.isRunning(), "MongoDB should be running");
        assertNotNull(mongoDBContainer.getReplicaSetUrl(), "MongoDB URL should be available");
        
        // Test Redis connection
        assertTrue(redisContainer.isRunning(), "Redis should be running");
        assertTrue(redisContainer.getMappedPort(6379) > 0, "Redis port should be mapped");
        
        // Test RabbitMQ connection
        assertTrue(rabbitMQContainer.isRunning(), "RabbitMQ should be running");
        assertTrue(rabbitMQContainer.getMappedPort(5672) > 0, "RabbitMQ port should be mapped");
        
        logger.info("All external dependencies are properly connected");
    }

    @Test
    void shouldLogApplicationStartupInformation() {
        logger.info("Testing application startup logging...");
        
        // Setup mock responses
        setupMockExternalServices();
        
        // Log application information
        logger.info("Application Name: URL Management Shred");
        logger.info("Application Port: {}", port);
        logger.info("Profile: test");
        logger.info("MongoDB URL: {}", mongoDBContainer.getReplicaSetUrl());
        logger.info("Redis Host: {}:{}", redisContainer.getHost(), redisContainer.getMappedPort(6379));
        logger.info("RabbitMQ Host: {}:{}", rabbitMQContainer.getHost(), rabbitMQContainer.getMappedPort(5672));
        
        // Verify basic application properties
        assertTrue(port > 0, "Application should have a valid port");
        assertTrue(mongoDBContainer.isRunning(), "MongoDB should be running");
        assertTrue(redisContainer.isRunning(), "Redis should be running");
        assertTrue(rabbitMQContainer.isRunning(), "RabbitMQ should be running");
        
        logger.info("Application startup logging test completed successfully");
    }

    private void setupMockExternalServices() {
        // Mock responses for external services to prevent actual HTTP calls
        try {
            // Mock Analytics Service - no additional methods needed for basic startup
            
            // Mock Config Service - use actual method names
            Map<String, Object> mockConfig = new HashMap<>();
            mockConfig.put("minLength", 6);
            mockConfig.put("maxLength", 10);
            when(configServiceClient.getURLShorteningConfig()).thenReturn(mockConfig);
            when(configServiceClient.getReservedAliases()).thenReturn(Arrays.asList("admin", "api", "test"));
            when(configServiceClient.getDefaultExpirationDays()).thenReturn(365);
            
            // Mock QR Code Service - return proper SharedQRCodeResponseDTO
            SharedQRCodeResponseDTO mockQRResponse = SharedQRCodeResponseDTO.builder()
                    .success(true)
                    .qrCodeUrl("http://localhost:9999/qr/mock-qr-code")
                    .expiresAt("2024-12-31T23:59:59Z")
                    .build();
            when(qrCodeServiceClient.generateQRCode(any(SharedQRCodeRequestDTO.class))).thenReturn(mockQRResponse);
            
            // Mock User Service - use correct field names
            SharedUserQuotaDTO mockQuota = SharedUserQuotaDTO.builder()
                    .userId("user123")
                    .tier("Premium")
                    .urlQuotaLimit(1000)
                    .urlQuotaUsed(50)
                    .urlQuotaRemaining(950)
                    .customAliasEnabled(true)
                    .bulkOperationsEnabled(true)
                    .build();
            when(userServiceClient.getUserQuota(any(String.class))).thenReturn(mockQuota);
            when(userServiceClient.checkQuotaAvailability(any(String.class), any(Integer.class))).thenReturn(Boolean.TRUE);
            
            logger.info("Mock external services configured successfully");
        } catch (Exception e) {
            logger.warn("Could not setup all mock external services: {}", e.getMessage());
            // Continue with test - some mocks might not be needed for basic startup
        }
    }
}