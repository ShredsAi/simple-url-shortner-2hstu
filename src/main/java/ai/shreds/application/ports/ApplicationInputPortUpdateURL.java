package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedUpdateURLRequestDTO;
import ai.shreds.shared.dtos.SharedURLResponseDTO;

public interface ApplicationInputPortUpdateURL {

    /**
     * Updates an existing URL's metadata, such as expiration date and tags.
     *
     * @param urlId   unique identifier of the URL to update
     * @param request update request containing new expiration date and tags
     * @return updated URL response DTO
     */
    SharedURLResponseDTO updateURL(String urlId, SharedUpdateURLRequestDTO request);
}
