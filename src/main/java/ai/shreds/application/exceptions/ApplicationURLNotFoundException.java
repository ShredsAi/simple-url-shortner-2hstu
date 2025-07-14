package ai.shreds.application.exceptions;

/**
 * Exception thrown when a URL is not found in the system.
 * This exception is thrown when attempting to retrieve, update, or delete a URL that doesn't exist.
 */
public class ApplicationURLNotFoundException extends ApplicationException {

    private final String urlId;

    /**
     * Constructs a new ApplicationURLNotFoundException with the specified URL ID.
     *
     * @param urlId the ID of the URL that was not found
     */
    public ApplicationURLNotFoundException(String urlId) {
        super(String.format("URL with ID '%s' not found", urlId));
        this.urlId = urlId;
    }

    /**
     * Constructs a new ApplicationURLNotFoundException with the specified URL ID and cause.
     *
     * @param urlId the ID of the URL that was not found
     * @param cause the cause of the exception
     */
    public ApplicationURLNotFoundException(String urlId, Throwable cause) {
        super(String.format("URL with ID '%s' not found", urlId), cause);
        this.urlId = urlId;
    }

    /**
     * Gets the URL ID that was not found.
     *
     * @return the URL ID
     */
    public String getUrlId() {
        return urlId;
    }
}