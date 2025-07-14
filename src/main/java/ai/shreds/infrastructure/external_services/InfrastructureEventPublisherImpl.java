package ai.shreds.infrastructure.external_services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ai.shreds.application.ports.ApplicationOutputPortEventPublisher;
import ai.shreds.shared.dtos.SharedURLCreatedEventDTO;
import ai.shreds.shared.dtos.SharedURLUpdatedEventDTO;
import ai.shreds.shared.dtos.SharedURLDeletedEventDTO;
import ai.shreds.shared.SharedBulkOperationCompletedEventDTO;
import ai.shreds.shared.SharedURLValidationRequestEventDTO;
import ai.shreds.infrastructure.utils.InfrastructureRetryUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InfrastructureEventPublisherImpl implements ApplicationOutputPortEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final InfrastructureRetryUtil retryUtil;

    @Value("${url.events.created.exchange}")
    private String urlCreatedExchange;

    @Value("${url.events.updated.exchange}")
    private String urlUpdatedExchange;

    @Value("${url.events.deleted.exchange}")
    private String urlDeletedExchange;

    @Value("${url.events.bulk.exchange}")
    private String bulkOperationExchange;

    @Value("${url.events.validation.exchange}")
    private String validationRequestExchange;

    public InfrastructureEventPublisherImpl(RabbitTemplate rabbitTemplate, InfrastructureRetryUtil retryUtil) {
        this.rabbitTemplate = rabbitTemplate;
        this.retryUtil = retryUtil;
    }

    @Override
    public void publishURLCreatedEvent(SharedURLCreatedEventDTO event) {
        publishWithRetry(urlCreatedExchange, "created", event);
    }

    @Override
    public void publishURLUpdatedEvent(SharedURLUpdatedEventDTO event) {
        publishWithRetry(urlUpdatedExchange, "updated", event);
    }

    @Override
    public void publishURLDeletedEvent(SharedURLDeletedEventDTO event) {
        publishWithRetry(urlDeletedExchange, "deleted", event);
    }

    @Override
    public void publishBulkOperationCompletedEvent(SharedBulkOperationCompletedEventDTO event) {
        publishWithRetry(bulkOperationExchange, "completed", event);
    }

    @Override
    public void publishURLValidationRequestEvent(SharedURLValidationRequestEventDTO event) {
        publishWithRetry(validationRequestExchange, "validate", event);
    }

    private void publishWithRetry(String exchange, String routingKey, Object event) {
        try {
            retryUtil.executeWithRetry(() -> {
                rabbitTemplate.convertAndSend(exchange, routingKey, event, message -> {
                    message.getMessageProperties().setHeader("x-event-type", event.getClass().getSimpleName());
                    message.getMessageProperties().setHeader("x-source-service", "url-management-service");
                    message.getMessageProperties().setHeader("x-correlation-id", java.util.UUID.randomUUID().toString());
                    message.getMessageProperties().setHeader("x-timestamp", System.currentTimeMillis());
                    return message;
                });
                return null;
            });
        } catch (Exception e) {
            log.error("Failed to publish event {} to exchange {}", event.getClass().getSimpleName(), exchange, e);
            throw new RuntimeException("Event publishing failed", e);
        }
    }
}