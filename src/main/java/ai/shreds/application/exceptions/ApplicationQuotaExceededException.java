package ai.shreds.application.exceptions;

/**
 * Exception thrown when a user attempts to exceed their quota limit.
 * This exception is thrown during URL creation when the user has insufficient quota.
 */
public class ApplicationQuotaExceededException extends ApplicationException {

    private final String userId;
    private final Integer requestedCount;
    private final Integer remainingQuota;

    /**
     * Constructs a new ApplicationQuotaExceededException.
     *
     * @param userId the ID of the user who exceeded quota
     * @param requestedCount the number of URLs requested
     * @param remainingQuota the remaining quota for the user
     */
    public ApplicationQuotaExceededException(String userId, Integer requestedCount, Integer remainingQuota) {
        super(String.format("User %s exceeded quota: requested %d URLs but only %d remaining", 
                          userId, requestedCount, remainingQuota));
        this.userId = userId;
        this.requestedCount = requestedCount;
        this.remainingQuota = remainingQuota;
    }

    /**
     * Gets the user ID who exceeded quota.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the number of URLs requested.
     *
     * @return the requested count
     */
    public Integer getRequestedCount() {
        return requestedCount;
    }

    /**
     * Gets the remaining quota for the user.
     *
     * @return the remaining quota
     */
    public Integer getRemainingQuota() {
        return remainingQuota;
    }
}