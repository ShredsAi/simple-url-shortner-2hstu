package ai.shreds.adapter.primary;

import ai.shreds.application.services.ApplicationURLService;
import ai.shreds.application.services.ApplicationBulkOperationService;
import ai.shreds.shared.dtos.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for URL management operations.
 * Handles HTTP requests for creating, reading, updating, and deleting URLs.
 * This is the primary adapter that converts HTTP requests to application service calls.
 */
@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
@Slf4j
public class AdapterURLController {

    private final ApplicationURLService urlService;
    private final ApplicationBulkOperationService bulkService;

    /**
     * Creates a new shortened URL.
     * 
     * @param request The URL creation request containing originalUrl, customAlias, expirationDate, tags, and generateQRCode flag
     * @return Created URL response with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<SharedURLResponseDTO> createURL(@Valid @RequestBody SharedCreateURLRequestDTO request) {
        log.info("Creating URL for originalUrl: {}, customAlias: {}, generateQRCode: {}", 
                request.getOriginalUrl(), request.getCustomAlias(), request.getGenerateQRCode());
        
        try {
            SharedURLResponseDTO response = urlService.createURL(request);
            log.info("Successfully created URL with ID: {}, shortCode: {}", 
                    response.getUrlId(), response.getShortCode());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating URL for originalUrl {}: {}", request.getOriginalUrl(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Retrieves a URL by its ID.
     * 
     * @param urlId The URL identifier
     * @return URL response with HTTP 200 status
     */
    @GetMapping("/{urlId}")
    public ResponseEntity<SharedURLResponseDTO> getURL(@PathVariable String urlId) {
        log.info("Retrieving URL with ID: {}", urlId);
        
        try {
            SharedURLResponseDTO response = urlService.getURL(urlId);
            log.info("Successfully retrieved URL with ID: {}, status: {}", 
                    urlId, response.getStatus());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving URL with ID {}: {}", urlId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Updates an existing URL's metadata.
     * 
     * @param urlId The URL identifier
     * @param request The update request containing expirationDate and tags
     * @return Updated URL response with HTTP 200 status
     */
    @PutMapping("/{urlId}")
    public ResponseEntity<SharedURLResponseDTO> updateURL(
            @PathVariable String urlId,
            @Valid @RequestBody SharedUpdateURLRequestDTO request) {
        log.info("Updating URL with ID: {}, expirationDate: {}, tags: {}", 
                urlId, request.getExpirationDate(), request.getTags());
        
        try {
            SharedURLResponseDTO response = urlService.updateURL(urlId, request);
            log.info("Successfully updated URL with ID: {}", urlId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating URL with ID {}: {}", urlId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Deletes a URL by its ID.
     * 
     * @param urlId The URL identifier
     * @return Deletion response with HTTP 200 status
     */
    @DeleteMapping("/{urlId}")
    public ResponseEntity<SharedDeleteURLResponseDTO> deleteURL(@PathVariable String urlId) {
        log.info("Deleting URL with ID: {}", urlId);
        
        try {
            SharedDeleteURLResponseDTO response = urlService.deleteURL(urlId);
            log.info("Successfully deleted URL with ID: {}, deletionType: {}", 
                    urlId, response.getDeletionType());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting URL with ID {}: {}", urlId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Creates multiple URLs in a single bulk operation.
     * 
     * @param request The bulk creation request containing array of URLs and common settings
     * @return Bulk operation response with HTTP 200 (all successful) or 207 (partial success) status
     */
    @PostMapping("/bulk")
    public ResponseEntity<SharedBulkOperationResponseDTO> bulkCreateURLs(
            @Valid @RequestBody SharedBulkCreateURLRequestDTO request) {
        log.info("Creating {} URLs in bulk operation, defaultExpirationDate: {}, generateQRCodes: {}", 
                request.getUrls().length, request.getDefaultExpirationDate(), request.getGenerateQRCodes());
        
        try {
            SharedBulkOperationResponseDTO response = bulkService.bulkCreateURLs(request);
            log.info("Bulk operation completed: {} successful, {} failed out of {} requested", 
                    response.getSuccessful(), response.getFailed(), response.getTotalRequested());
            
            // Return 207 Multi-Status if there were partial failures
            if (response.getFailed() > 0) {
                return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(response);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in bulk URL creation: {}", e.getMessage(), e);
            throw e;
        }
    }
}