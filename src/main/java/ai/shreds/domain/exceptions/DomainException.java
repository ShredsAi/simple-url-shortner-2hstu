package ai.shreds.domain.exceptions;

/**
 * Base exception class for all domain-related exceptions.
 * Represents business rule violations and domain-specific errors.
 */
public class DomainException extends RuntimeException {
    
    /**
     * Creates a new domain exception with the specified message.
     * 
     * @param message The error message
     */
    public DomainException(String message) {
        super(message);
    }
    
    /**
     * Creates a new domain exception with the specified message and cause.
     * 
     * @param message The error message
     * @param cause The underlying cause
     */
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Creates a new domain exception with the specified cause.
     * 
     * @param cause The underlying cause
     */
    public DomainException(Throwable cause) {
        super(cause);
    }
}