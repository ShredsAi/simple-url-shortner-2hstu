package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedURLCreatedEventDTO;
import ai.shreds.shared.dtos.SharedURLUpdatedEventDTO;
import ai.shreds.shared.dtos.SharedURLDeletedEventDTO;

public interface ApplicationOutputPortAnalyticsService {

    /**
     * Tracks URL creation events for analytics.
     *
     * @param event the URL created event data
     */
    void trackURLCreated(SharedURLCreatedEventDTO event);

    /**
     * Tracks URL update events for analytics.
     *
     * @param event the URL updated event data
     */
    void trackURLUpdated(SharedURLUpdatedEventDTO event);

    /**
     * Tracks URL deletion events for analytics.
     *
     * @param event the URL deleted event data
     */
    void trackURLDeleted(SharedURLDeletedEventDTO event);
}
