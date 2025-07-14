package ai.shreds.adapter.exceptions;

/**
 * Exception thrown when validation fails in the adapter layer.
 * This exception is used to handle validation errors that occur when processing
 * incoming requests or events in the adapter layer.
 */
public class AdapterValidationException extends RuntimeException {

    private final String urlId;

    /**
     * Constructs a new AdapterValidationException with the specified detail message and URL ID.
     *
     * @param message the detail message
     * @param urlId the URL ID associated with the validation failure
     */
    public AdapterValidationException(String message, String urlId) {
        super(message);
        this.urlId = urlId;
    }

    /**
     * Constructs a new AdapterValidationException with the specified detail message, URL ID, and cause.
     *
     * @param message the detail message
     * @param urlId the URL ID associated with the validation failure
     * @param cause the cause of the exception
     */
    public AdapterValidationException(String message, String urlId, Throwable cause) {
        super(message, cause);
        this.urlId = urlId;
    }

    /**
     * Returns the URL ID associated with this validation exception.
     *
     * @return the URL ID, or null if not available
     */
    public String getUrlId() {
        return urlId;
    }

    @Override
    public String getMessage() {
        String baseMessage = super.getMessage();
        if (urlId != null && !urlId.equals("unknown")) {
            return baseMessage + " [URL ID: " + urlId + "]";
        }
        return baseMessage;
    }
}