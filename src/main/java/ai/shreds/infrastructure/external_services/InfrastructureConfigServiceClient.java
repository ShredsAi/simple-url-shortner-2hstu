package ai.shreds.infrastructure.external_services;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ai.shreds.application.ports.ApplicationOutputPortConfigService;
import ai.shreds.infrastructure.utils.InfrastructureCircuitBreakerUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InfrastructureConfigServiceClient implements ApplicationOutputPortConfigService {

    private final RestTemplate restTemplate;
    private final InfrastructureCircuitBreakerUtil circuitBreaker;
    private final String configServiceUrl;

    public InfrastructureConfigServiceClient(
            @Value("${config.service.url}") String configServiceUrl,
            InfrastructureCircuitBreakerUtil circuitBreaker) {
        this.configServiceUrl = configServiceUrl;
        this.circuitBreaker = circuitBreaker;
        this.restTemplate = new RestTemplate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getURLShorteningConfig() {
        try {
            return circuitBreaker.execute(() ->
                restTemplate.getForObject(
                    configServiceUrl + "/configurations/url-service/shortening",
                    Map.class
                )
            );
        } catch (Exception e) {
            log.error("Error fetching URL shortening configuration", e);
            throw new RuntimeException("Failed to fetch URL shortening configuration", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getReservedAliases() {
        try {
            return circuitBreaker.execute(() ->
                restTemplate.getForObject(
                    configServiceUrl + "/configurations/url-service/reserved-aliases",
                    List.class
                )
            );
        } catch (Exception e) {
            log.error("Error fetching reserved aliases", e);
            throw new RuntimeException("Failed to fetch reserved aliases", e);
        }
    }

    @Override
    public Integer getDefaultExpirationDays() {
        try {
            return circuitBreaker.execute(() ->
                restTemplate.getForObject(
                    configServiceUrl + "/configurations/url-service/default-expiration-days",
                    Integer.class
                )
            );
        } catch (Exception e) {
            log.error("Error fetching default expiration days", e);
            return 365; // Default fallback value
        }
    }
}
