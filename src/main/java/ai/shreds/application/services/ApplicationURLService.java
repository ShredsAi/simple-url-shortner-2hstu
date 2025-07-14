package ai.shreds.application.services;

import ai.shreds.application.exceptions.*;
import ai.shreds.application.ports.*;
import ai.shreds.domain.ports.*;
import ai.shreds.shared.dtos.*;
import ai.shreds.shared.value_objects.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ApplicationURLService implements ApplicationInputPortCreateURL, ApplicationInputPortGetURL, ApplicationInputPortUpdateURL, ApplicationInputPortDeleteURL {

    private static final Logger log = LoggerFactory.getLogger(ApplicationURLService.class);
    
    private final DomainInputPortURLService domainURLService;
    private final ApplicationQuotaValidationService quotaValidationService;
    private final ApplicationEventPublishingService eventPublishingService;
    private final ApplicationOutputPortQRCodeService qrCodeService;
    private final ApplicationOutputPortConfigService configService;

    @Override
    @Transactional
    public SharedURLResponseDTO createURL(SharedCreateURLRequestDTO request) {
        String userId = getCurrentUserId();
        log.info("Creating URL for user: {}, originalUrl: {}", userId, request.getOriginalUrl());

        // Validate quota first
        if (!quotaValidationService.validateQuota(userId, 1)) {
            throw new ApplicationQuotaExceededException(userId, 1, quotaValidationService.getUserRemainingQuota(userId));
        }

        // Check if user can use custom alias
        if (request.getCustomAlias() != null && !quotaValidationService.canUseCustomAlias(userId)) {
            throw new ApplicationAccessDeniedException(null, userId);
        }

        // Get configuration
        Integer defaultExpirationDays = configService.getDefaultExpirationDays();
        Date expirationDate = request.getExpirationDate() != null ? 
            parseDate(request.getExpirationDate()) : 
            new Date(System.currentTimeMillis() + defaultExpirationDays * 24 * 60 * 60 * 1000L);

        try {
            // Create URL through domain service
            var urlEntity = domainURLService.createURL(
                request.getOriginalUrl(),
                null, // Short code will be generated
                userId,
                request.getCustomAlias(),
                expirationDate,
                request.getTags() != null ? Arrays.asList(request.getTags()) : Collections.emptyList()
            );

            // Build shortened URL
            String shortenedUrl = buildShortenedUrl(urlEntity.getShortCode());
            String qrCodeUrl = null;

            // Generate QR code if requested
            if (request.getGenerateQRCode() != null && request.getGenerateQRCode()) {
                qrCodeUrl = generateQRCodeSafely(shortenedUrl);
            }

            // Create response DTO manually since we don't have access to domain metadata entity
            var responseDTO = new SharedURLResponseDTO();
            responseDTO.setUrlId(urlEntity.getUrlId());
            responseDTO.setOriginalUrl(urlEntity.getOriginalUrl());
            responseDTO.setShortCode(urlEntity.getShortCode());
            responseDTO.setShortenedUrl(shortenedUrl);
            responseDTO.setCustomAlias(urlEntity.getCustomAlias());
            responseDTO.setStatus(urlEntity.getStatus());
            responseDTO.setQrCodeUrl(qrCodeUrl);

            // Create metadata DTO
            var metadataDTO = new SharedURLMetadataDTO();
            metadataDTO.setCreationDate(urlEntity.getCreatedAt().toString());
            metadataDTO.setExpirationDate(expirationDate.toString());
            metadataDTO.setAccessCount(0L);
            metadataDTO.setTags(request.getTags());
            responseDTO.setMetadata(metadataDTO);

            // Publish event
            eventPublishingService.publishURLCreated(responseDTO);

            // Request validation
            eventPublishingService.requestURLValidation(urlEntity.getUrlId(), request.getOriginalUrl());

            log.info("URL created successfully with ID: {}", urlEntity.getUrlId());
            return responseDTO;

        } catch (Exception e) {
            log.error("Failed to create URL for user: {}", userId, e);
            throw new ApplicationException("Failed to create URL", e);
        }
    }

    @Override
    public SharedURLResponseDTO getURL(String urlId) {
        log.info("Retrieving URL with ID: {}", urlId);

        var urlEntity = domainURLService.getURL(urlId);
        if (urlEntity == null) {
            throw new ApplicationURLNotFoundException(urlId);
        }

        // Check access permissions
        String userId = getCurrentUserId();
        if (!urlEntity.canBeAccessedBy(userId)) {
            throw new ApplicationAccessDeniedException(urlId, userId);
        }

        String shortenedUrl = buildShortenedUrl(urlEntity.getShortCode());

        // Build response DTO
        var responseDTO = new SharedURLResponseDTO();
        responseDTO.setUrlId(urlEntity.getUrlId());
        responseDTO.setOriginalUrl(urlEntity.getOriginalUrl());
        responseDTO.setShortCode(urlEntity.getShortCode());
        responseDTO.setShortenedUrl(shortenedUrl);
        responseDTO.setCustomAlias(urlEntity.getCustomAlias());
        responseDTO.setStatus(urlEntity.getStatus());

        // Create metadata DTO
        var metadataDTO = new SharedURLMetadataDTO();
        metadataDTO.setCreationDate(urlEntity.getCreatedAt().toString());
        metadataDTO.setExpirationDate(urlEntity.getUpdatedAt().toString());
        metadataDTO.setAccessCount(0L); // Would need to get from metadata entity
        metadataDTO.setTags(new String[0]); // Would need to get from metadata entity
        responseDTO.setMetadata(metadataDTO);

        return responseDTO;
    }

    @Override
    @Transactional
    public SharedURLResponseDTO updateURL(String urlId, SharedUpdateURLRequestDTO request) {
        log.info("Updating URL with ID: {}", urlId);

        String userId = getCurrentUserId();

        // Get the URL first to check permissions
        var existingEntity = domainURLService.getURL(urlId);
        if (existingEntity == null) {
            throw new ApplicationURLNotFoundException(urlId);
        }

        if (!existingEntity.canBeAccessedBy(userId)) {
            throw new ApplicationAccessDeniedException(urlId, userId);
        }

        try {
            Date expirationDate = request.getExpirationDate() != null ? 
                parseDate(request.getExpirationDate()) : null;

            List<String> tags = request.getTags() != null ? Arrays.asList(request.getTags()) : null;

            var updatedEntity = domainURLService.updateURL(urlId, expirationDate, tags);

            String shortenedUrl = buildShortenedUrl(updatedEntity.getShortCode());

            // Build response DTO
            var responseDTO = new SharedURLResponseDTO();
            responseDTO.setUrlId(updatedEntity.getUrlId());
            responseDTO.setOriginalUrl(updatedEntity.getOriginalUrl());
            responseDTO.setShortCode(updatedEntity.getShortCode());
            responseDTO.setShortenedUrl(shortenedUrl);
            responseDTO.setCustomAlias(updatedEntity.getCustomAlias());
            responseDTO.setStatus(updatedEntity.getStatus());

            // Create metadata DTO
            var metadataDTO = new SharedURLMetadataDTO();
            metadataDTO.setCreationDate(updatedEntity.getCreatedAt().toString());
            metadataDTO.setExpirationDate(expirationDate != null ? expirationDate.toString() : null);
            metadataDTO.setAccessCount(0L);
            metadataDTO.setTags(request.getTags());
            responseDTO.setMetadata(metadataDTO);

            // Publish update event
            eventPublishingService.publishURLUpdated(responseDTO);

            log.info("URL updated successfully with ID: {}", urlId);
            return responseDTO;

        } catch (Exception e) {
            log.error("Failed to update URL with ID: {}", urlId, e);
            throw new ApplicationException("Failed to update URL", e);
        }
    }

    @Override
    @Transactional
    public SharedDeleteURLResponseDTO deleteURL(String urlId) {
        log.info("Deleting URL with ID: {}", urlId);

        String userId = getCurrentUserId();

        // Get URL first to check permissions
        var urlEntity = domainURLService.getURL(urlId);
        if (urlEntity == null) {
            throw new ApplicationURLNotFoundException(urlId);
        }

        if (!urlEntity.canBeAccessedBy(userId)) {
            throw new ApplicationAccessDeniedException(urlId, userId);
        }

        try {
            boolean success = domainURLService.deleteURL(urlId, "SOFT_DELETE");

            var response = new SharedDeleteURLResponseDTO();
            response.setSuccess(success);
            response.setMessage(success ? "URL deleted successfully" : "Failed to delete URL");
            response.setUrlId(urlId);
            response.setDeletionType("SOFT_DELETE");

            if (success) {
                // Publish deletion event
                eventPublishingService.publishURLDeleted(urlId, "SOFT_DELETE");
                log.info("URL deleted successfully with ID: {}", urlId);
            }

            return response;

        } catch (Exception e) {
            log.error("Failed to delete URL with ID: {}", urlId, e);
            throw new ApplicationException("Failed to delete URL", e);
        }
    }

    private String getCurrentUserId() {
        // TODO: Get from Spring Security context
        return "user123"; // Placeholder
    }

    private Date parseDate(String dateStr) {
        try {
            // Parse ISO-8601 format
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return java.sql.Timestamp.valueOf(dateTime);
        } catch (Exception e) {
            log.warn("Failed to parse date: {}, using current date", dateStr);
            return new Date();
        }
    }

    private String buildShortenedUrl(String shortCode) {
        // TODO: Get base URL from configuration
        return "https://short.ly/" + shortCode;
    }

    private String generateQRCodeSafely(String shortenedUrl) {
        try {
            var qrRequest = new SharedQRCodeRequestDTO();
            qrRequest.setContent(shortenedUrl);
            qrRequest.setSize(300);
            qrRequest.setErrorCorrection("M");
            qrRequest.setFormat("png");

            var qrResponse = qrCodeService.generateQRCode(qrRequest);
            if (qrResponse.getSuccess()) {
                return qrResponse.getQrCodeUrl();
            }
        } catch (Exception e) {
            log.warn("Failed to generate QR code for URL: {}", shortenedUrl, e);
        }
        return null;
    }
}