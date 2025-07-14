package ai.shreds.infrastructure.external_services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ai.shreds.application.ports.ApplicationOutputPortAnalyticsService;
import ai.shreds.shared.dtos.SharedURLCreatedEventDTO;
import ai.shreds.shared.dtos.SharedURLUpdatedEventDTO;
import ai.shreds.shared.dtos.SharedURLDeletedEventDTO;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * External service client for analytics tracking.
 * Sends URL events to the analytics service for tracking and reporting.
 */
@Slf4j
@Service
public class InfrastructureAnalyticsServiceClient implements ApplicationOutputPortAnalyticsService {

    private final RestTemplate restTemplate;
    private final String analyticsServiceUrl;

    public InfrastructureAnalyticsServiceClient(
            @Value("${external.services.analytics-service.url}") String analyticsServiceUrl) {
        this.analyticsServiceUrl = analyticsServiceUrl;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void trackURLCreated(SharedURLCreatedEventDTO event) {
        log.debug("Tracking URL created event: {}", event.getUrlId());
        sendEventAsync(event, "URL_CREATED");
    }

    @Override
    public void trackURLUpdated(SharedURLUpdatedEventDTO event) {
        log.debug("Tracking URL updated event: {}", event.getUrlId());
        sendEventAsync(event, "URL_UPDATED");
    }

    @Override
    public void trackURLDeleted(SharedURLDeletedEventDTO event) {
        log.debug("Tracking URL deleted event: {}", event.getUrlId());
        sendEventAsync(event, "URL_DELETED");
    }

    /**
     * Sends analytics event asynchronously to avoid blocking the main flow.
     * 
     * @param event The event to send
     * @param eventType The type of event for categorization
     */
    private void sendEventAsync(Object event, String eventType) {
        CompletableFuture.runAsync(() -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("X-Event-Type", eventType);
                headers.set("X-Source-Service", "url-management-service");
                headers.set("X-Correlation-ID", java.util.UUID.randomUUID().toString());
                
                HttpEntity<Object> request = new HttpEntity<>(event, headers);
                
                restTemplate.postForEntity(
                    analyticsServiceUrl + "/events/url", 
                    request, 
                    String.class
                );
                
                log.debug("Successfully sent {} event to analytics service", eventType);
                
            } catch (Exception e) {
                // Log error but don't fail the main operation
                log.warn("Failed to send {} event to analytics service: {}", eventType, e.getMessage());
                // In a production system, you might want to queue failed events for retry
            }
        });
    }
}