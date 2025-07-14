package ai.shreds.application.exceptions;

import java.util.List;

/**
 * Exception thrown when URL validation fails during the validation process.
 * This exception is thrown when a URL fails security validation or other validation checks.
 */
public class ApplicationValidationFailedException extends ApplicationException {

    private final String urlId;
    private final String originalUrl;
    private final List<String> validationErrors;
    private final Integer securityScore;

    /**
     * Constructs a new ApplicationValidationFailedException.
     *
     * @param urlId the ID of the URL that failed validation
     * @param originalUrl the original URL that failed validation
     * @param validationErrors list of validation error messages
     * @param securityScore the security score assigned to the URL
     */
    public ApplicationValidationFailedException(String urlId, String originalUrl, List<String> validationErrors, Integer securityScore) {
        super(String.format("URL validation failed for URL ID '%s' (original: %s). Security score: %d. Errors: %s", 
                          urlId, originalUrl, securityScore, String.join(", ", validationErrors)));
        this.urlId = urlId;
        this.originalUrl = originalUrl;
        this.validationErrors = validationErrors;
        this.securityScore = securityScore;
    }

    /**
     * Constructs a new ApplicationValidationFailedException with cause.
     *
     * @param urlId the ID of the URL that failed validation
     * @param originalUrl the original URL that failed validation
     * @param validationErrors list of validation error messages
     * @param securityScore the security score assigned to the URL
     * @param cause the underlying cause of the exception
     */
    public ApplicationValidationFailedException(String urlId, String originalUrl, List<String> validationErrors, Integer securityScore, Throwable cause) {
        super(String.format("URL validation failed for URL ID '%s' (original: %s). Security score: %d. Errors: %s", 
                          urlId, originalUrl, securityScore, String.join(", ", validationErrors)), cause);
        this.urlId = urlId;
        this.originalUrl = originalUrl;
        this.validationErrors = validationErrors;
        this.securityScore = securityScore;
    }

    /**
     * Gets the URL ID that failed validation.
     *
     * @return the URL ID
     */
    public String getUrlId() {
        return urlId;
    }

    /**
     * Gets the original URL that failed validation.
     *
     * @return the original URL
     */
    public String getOriginalUrl() {
        return originalUrl;
    }

    /**
     * Gets the list of validation error messages.
     *
     * @return the validation errors
     */
    public List<String> getValidationErrors() {
        return validationErrors;
    }

    /**
     * Gets the security score assigned to the URL.
     *
     * @return the security score
     */
    public Integer getSecurityScore() {
        return securityScore;
    }
}