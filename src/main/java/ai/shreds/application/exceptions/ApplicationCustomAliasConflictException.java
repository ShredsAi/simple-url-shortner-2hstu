package ai.shreds.application.exceptions;

/**
 * Exception thrown when a custom alias conflicts with an existing alias or reserved word.
 * This exception is thrown during URL creation when the requested custom alias is not available.
 */
public class ApplicationCustomAliasConflictException extends ApplicationException {

    private final String requestedAlias;
    private final String conflictType;
    private final String userId;

    /**
     * Constructs a new ApplicationCustomAliasConflictException for alias conflicts.
     *
     * @param requestedAlias the custom alias that caused the conflict
     * @param conflictType the type of conflict (e.g., "ALREADY_EXISTS", "RESERVED_WORD")
     * @param userId the ID of the user who requested the alias
     */
    public ApplicationCustomAliasConflictException(String requestedAlias, String conflictType, String userId) {
        super(String.format("Custom alias '%s' conflicts with existing data: %s (requested by user: %s)", 
                          requestedAlias, conflictType, userId));
        this.requestedAlias = requestedAlias;
        this.conflictType = conflictType;
        this.userId = userId;
    }

    /**
     * Constructs a new ApplicationCustomAliasConflictException for alias conflicts with cause.
     *
     * @param requestedAlias the custom alias that caused the conflict
     * @param conflictType the type of conflict (e.g., "ALREADY_EXISTS", "RESERVED_WORD")
     * @param userId the ID of the user who requested the alias
     * @param cause the underlying cause of the exception
     */
    public ApplicationCustomAliasConflictException(String requestedAlias, String conflictType, String userId, Throwable cause) {
        super(String.format("Custom alias '%s' conflicts with existing data: %s (requested by user: %s)", 
                          requestedAlias, conflictType, userId), cause);
        this.requestedAlias = requestedAlias;
        this.conflictType = conflictType;
        this.userId = userId;
    }

    /**
     * Gets the custom alias that caused the conflict.
     *
     * @return the requested alias
     */
    public String getRequestedAlias() {
        return requestedAlias;
    }

    /**
     * Gets the type of conflict that occurred.
     *
     * @return the conflict type
     */
    public String getConflictType() {
        return conflictType;
    }

    /**
     * Gets the user ID who requested the conflicting alias.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }
}