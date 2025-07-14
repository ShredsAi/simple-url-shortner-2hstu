package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedURLCreatedEventDTO;
import ai.shreds.shared.dtos.SharedURLUpdatedEventDTO;
import ai.shreds.shared.dtos.SharedURLDeletedEventDTO;
import ai.shreds.shared.dtos.SharedBulkOperationCompletedEventDTO;
import ai.shreds.shared.dtos.SharedURLValidationRequestEventDTO;

public interface ApplicationOutputPortEventPublisher {

    /**
     * Publishes an event when a new URL is created.
     *
     * @param event the URL created event data
     */
    void publishURLCreatedEvent(SharedURLCreatedEventDTO event);

    /**
     * Publishes an event when a URL is updated.
     *
     * @param event the URL updated event data
     */
    void publishURLUpdatedEvent(SharedURLUpdatedEventDTO event);

    /**
     * Publishes an event when a URL is deleted.
     *
     * @param event the URL deleted event data
     */
    void publishURLDeletedEvent(SharedURLDeletedEventDTO event);

    /**
     * Publishes an event when a bulk operation is completed.
     *
     * @param event the bulk operation completed event data
     */
    void publishBulkOperationCompletedEvent(SharedBulkOperationCompletedEventDTO event);

    /**
     * Publishes an event to request URL validation.
     *
     * @param event the URL validation request event data
     */
    void publishURLValidationRequestEvent(SharedURLValidationRequestEventDTO event);
}