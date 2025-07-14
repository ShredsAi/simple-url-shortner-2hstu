package ai.shreds.infrastructure.external_services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ai.shreds.application.ports.ApplicationOutputPortUserService;
import ai.shreds.shared.dtos.SharedUserQuotaDTO;
import ai.shreds.infrastructure.utils.InfrastructureCircuitBreakerUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Slf4j
@Service
public class InfrastructureUserServiceClient implements ApplicationOutputPortUserService {

    private final RestTemplate restTemplate;
    private final InfrastructureCircuitBreakerUtil circuitBreaker;
    private final String userServiceUrl;

    public InfrastructureUserServiceClient(
            @Value("${external.services.user-service.url:http://localhost:8091}") String userServiceUrl,
            InfrastructureCircuitBreakerUtil circuitBreaker) {
        this.userServiceUrl = userServiceUrl;
        this.circuitBreaker = circuitBreaker;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public SharedUserQuotaDTO getUserQuota(String userId) {
        try {
            return circuitBreaker.execute(() ->
                restTemplate.getForObject(
                    userServiceUrl + "/users/{userId}/quotas/url",
                    SharedUserQuotaDTO.class,
                    userId
                )
            );
        } catch (Exception e) {
            log.error("Error fetching user quota for userId: {}", userId, e);
            throw new RuntimeException("Failed to fetch user quota", e);
        }
    }

    @Override
    public Boolean checkQuotaAvailability(String userId, Integer requestedCount) {
        try {
            return circuitBreaker.execute(() -> {
                var response = restTemplate.postForObject(
                    userServiceUrl + "/users/{userId}/quotas/url/check",
                    Map.of("requestedCount", requestedCount, "operation", "CREATE"),
                    QuotaCheckResponse.class,
                    userId
                );
                return Boolean.valueOf(response != null && response.isAllowed());
            });
        } catch (Exception e) {
            log.error("Error checking quota availability for userId: {}", userId, e);
            return Boolean.FALSE; // Fail-safe approach with Boolean object
        }
    }

    // Removed @Override annotation since this method is not in the interface
    public boolean validateUser(String userId) {
        try {
            return circuitBreaker.execute(() -> {
                var response = restTemplate.getForObject(
                    userServiceUrl + "/users/{userId}/validate",
                    Map.class,
                    userId
                );
                return response != null && Boolean.TRUE.equals(response.get("valid"));
            });
        } catch (Exception e) {
            log.error("Error validating user: {}", userId, e);
            return false;
        }
    }

    private static class QuotaCheckResponse {
        private boolean allowed;
        private int remainingQuota;

        public boolean isAllowed() {
            return allowed;
        }

        public void setAllowed(boolean allowed) {
            this.allowed = allowed;
        }

        public int getRemainingQuota() {
            return remainingQuota;
        }

        public void setRemainingQuota(int remainingQuota) {
            this.remainingQuota = remainingQuota;
        }
    }
}