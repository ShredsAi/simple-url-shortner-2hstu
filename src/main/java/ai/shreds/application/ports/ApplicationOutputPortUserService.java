package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedUserQuotaDTO;

/**
 * Output port for user service operations.
 * This interface defines the contract for retrieving user information and quota data.
 */
public interface ApplicationOutputPortUserService {

    /**
     * Retrieves user quota information including limits, usage, and permissions.
     *
     * @param userId the unique identifier of the user
     * @return the user quota information
     */
    SharedUserQuotaDTO getUserQuota(String userId);

    /**
     * Checks if a user has sufficient quota available for the requested count.
     *
     * @param userId the unique identifier of the user
     * @param requestedCount the number of URLs requested to be created
     * @return true if the user has sufficient quota, false otherwise
     */
    Boolean checkQuotaAvailability(String userId, Integer requestedCount);
}