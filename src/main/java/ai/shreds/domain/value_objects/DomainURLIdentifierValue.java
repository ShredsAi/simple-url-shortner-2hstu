package ai.shreds.domain.value_objects;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a URL identifier.
 * Ensures the identifier is valid and provides proper value equality semantics.
 */
public final class DomainURLIdentifierValue {
    
    private final String value;
    
    /**
     * Creates a new URL identifier with the specified value.
     * 
     * @param value The identifier value, must not be null or empty
     * @throws IllegalArgumentException if the value is null or empty
     */
    public DomainURLIdentifierValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("URL identifier cannot be null or empty");
        }
        this.value = value;
    }
    
    /**
     * Creates a new random URL identifier.
     * 
     * @return a new DomainURLIdentifierValue with a random UUID
     */
    public static DomainURLIdentifierValue createNew() {
        return new DomainURLIdentifierValue(UUID.randomUUID().toString());
    }
    
    /**
     * Gets the identifier value.
     * 
     * @return The identifier value as a String
     */
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        DomainURLIdentifierValue that = (DomainURLIdentifierValue) other;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}