package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedBulkCreateURLRequestDTO;
import ai.shreds.shared.dtos.SharedBulkOperationResponseDTO;

public interface ApplicationInputPortBulkCreateURLs {

    /**
     * Processes a bulk creation of URLs.
     *
     * @param request bulk create request containing URLs and options
     * @return response with total requested, successful, failed counts, results and errors
     */
    SharedBulkOperationResponseDTO bulkCreateURLs(SharedBulkCreateURLRequestDTO request);
}
