package ai.shreds.shared.value_objects;

/**
 * Enum representing types of operations that can be performed in bulk.
 * Used to specify the operation type in bulk requests and events.
 */
public enum SharedEnumOperationType {
    /** Create operation for adding new URLs */
    CREATE,

    /** Update operation for modifying existing URLs */
    UPDATE,

    /** Delete operation for removing URLs */
    DELETE
}