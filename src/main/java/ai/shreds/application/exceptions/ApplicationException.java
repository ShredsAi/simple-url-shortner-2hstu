package ai.shreds.application.exceptions;

/**
 * Base exception class for all application layer exceptions.
 * This serves as the parent class for all custom exceptions in the application layer.
 */
public class ApplicationException extends RuntimeException {

    /**
     * Constructs a new ApplicationException with the specified detail message.
     *
     * @param message the detail message
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * Constructs a new ApplicationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ApplicationException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public ApplicationException(Throwable cause) {
        super(cause);
    }
}