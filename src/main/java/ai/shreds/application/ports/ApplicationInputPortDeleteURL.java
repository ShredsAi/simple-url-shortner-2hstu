package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedDeleteURLResponseDTO;

public interface ApplicationInputPortDeleteURL {

    /**
     * Deletes a URL by its unique identifier.
     * Performs soft deletion by default, marking the URL as deleted rather than removing it.
     *
     * @param urlId the unique identifier of the URL to delete
     * @return response containing deletion status and details
     */
    SharedDeleteURLResponseDTO deleteURL(String urlId);
}
