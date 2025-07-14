package ai.shreds.application.ports;

import java.util.List;
import java.util.Map;

/**
 * Output port for configuration service operations.
 * This interface defines the contract for retrieving application configuration data.
 */
public interface ApplicationOutputPortConfigService {

    /**
     * Retrieves URL shortening configuration parameters.
     *
     * @return a map containing configuration key-value pairs
     */
    Map<String, Object> getURLShorteningConfig();

    /**
     * Retrieves the list of reserved aliases that cannot be used by users.
     *
     * @return a list of reserved alias strings
     */
    List<String> getReservedAliases();

    /**
     * Retrieves the default number of days for URL expiration.
     *
     * @return the default expiration period in days
     */
    Integer getDefaultExpirationDays();
}