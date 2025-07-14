package ai.shreds.application.exceptions;

/**
 * Exception thrown when a user attempts to access a resource they don't have permission to access.
 * This exception is thrown when access control validation fails.
 */
public class ApplicationAccessDeniedException extends ApplicationException {

    private final String resourceId;
    private final String userId;

    /**
     * Constructs a new ApplicationAccessDeniedException with the specified resource ID and user ID.
     *
     * @param resourceId the ID of the resource that access was denied to (can be null)
     * @param userId the ID of the user who was denied access
     */
    public ApplicationAccessDeniedException(String resourceId, String userId) {
        super(String.format("Access denied for user '%s' to resource '%s'", userId, resourceId));
        this.resourceId = resourceId;
        this.userId = userId;
    }

    /**
     * Constructs a new ApplicationAccessDeniedException with the specified resource ID, user ID, and cause.
     *
     * @param resourceId the ID of the resource that access was denied to (can be null)
     * @param userId the ID of the user who was denied access
     * @param cause the cause of the exception
     */
    public ApplicationAccessDeniedException(String resourceId, String userId, Throwable cause) {
        super(String.format("Access denied for user '%s' to resource '%s'", userId, resourceId), cause);
        this.resourceId = resourceId;
        this.userId = userId;
    }

    /**
     * Gets the resource ID that access was denied to.
     *
     * @return the resource ID (can be null)
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Gets the user ID who was denied access.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }
}