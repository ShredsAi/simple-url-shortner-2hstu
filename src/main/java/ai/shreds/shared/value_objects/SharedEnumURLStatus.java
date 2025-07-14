package ai.shreds.shared.value_objects;

/**
 * Enum representing the possible states of a URL in the system.
 */
public enum SharedEnumURLStatus {
    /**
     * URL is active and can be accessed
     */
    ACTIVE,

    /**
     * URL has reached its expiration date
     */
    EXPIRED,

    /**
     * URL has been manually deactivated
     */
    DEACTIVATED,

    /**
     * URL has been marked as deleted
     */
    DELETED
}