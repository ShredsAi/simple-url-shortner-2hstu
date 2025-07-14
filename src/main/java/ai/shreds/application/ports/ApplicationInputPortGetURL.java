package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedURLResponseDTO;

/**
 * Input port for retrieving URLs.
 * This interface defines the contract for URL retrieval operations.
 */
public interface ApplicationInputPortGetURL {

    /**
     * Retrieves a URL by its unique identifier.
     *
     * @param urlId the unique identifier of the URL to retrieve
     * @return the URL response with all associated data
     */
    SharedURLResponseDTO getURL(String urlId);
}