package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedCreateURLRequestDTO;
import ai.shreds.shared.dtos.SharedURLResponseDTO;

/**
 * Input port for creating new URLs.
 * This interface defines the contract for URL creation operations.
 */
public interface ApplicationInputPortCreateURL {

    /**
     * Creates a new shortened URL.
     *
     * @param request the URL creation request containing original URL, custom alias, expiration date, tags, and QR code generation flag
     * @return the created URL response with all generated data
     */
    SharedURLResponseDTO createURL(SharedCreateURLRequestDTO request);
}