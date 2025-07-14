package ai.shreds.application.services;

import ai.shreds.application.ports.ApplicationOutputPortUserService;
import ai.shreds.shared.dtos.SharedUserQuotaDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationQuotaValidationService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationQuotaValidationService.class);
    
    private final ApplicationOutputPortUserService userService;

    /**
     * Validates if a user has sufficient quota for the requested operation.
     *
     * @param userId The user ID to check quota for
     * @param requestedCount The number of URLs requested to be created
     * @return true if the user has sufficient quota, false otherwise
     */
    public boolean validateQuota(String userId, Integer requestedCount) {
        log.info("Validating quota for user: {}, requested count: {}", userId, requestedCount);
        
        try {
            // Get user quota information
            SharedUserQuotaDTO userQuota = userService.getUserQuota(userId);
            
            if (userQuota == null) {
                log.warn("No quota information found for user: {}", userId);
                return false;
            }
            
            // Check if user has enough remaining quota
            boolean hasQuota = userQuota.getUrlQuotaRemaining() >= requestedCount;
            
            if (!hasQuota) {
                log.warn("Quota validation failed for user: {}. Requested: {}, Remaining: {}", 
                         userId, requestedCount, userQuota.getUrlQuotaRemaining());
                return false;
            }
            
            // For bulk operations, check if bulk operations are enabled for this user
            if (requestedCount > 1 && !userQuota.getBulkOperationsEnabled()) {
                log.warn("Bulk operations not enabled for user: {}", userId);
                return false;
            }
            
            log.info("Quota validation passed for user: {}. Remaining after operation: {}", 
                     userId, userQuota.getUrlQuotaRemaining() - requestedCount);
            return true;
            
        } catch (Exception e) {
            log.error("Error validating quota for user: {}", userId, e);
            // In case of error, we might want to fail gracefully or allow the operation
            // depending on the business requirements
            return false;
        }
    }

    /**
     * Gets the remaining quota for a user.
     *
     * @param userId The user ID to check quota for
     * @return The remaining quota count, or 0 if unable to retrieve
     */
    public Integer getUserRemainingQuota(String userId) {
        log.debug("Getting remaining quota for user: {}", userId);
        
        try {
            SharedUserQuotaDTO userQuota = userService.getUserQuota(userId);
            
            if (userQuota == null) {
                log.warn("No quota information found for user: {}", userId);
                return 0;
            }
            
            return userQuota.getUrlQuotaRemaining();
            
        } catch (Exception e) {
            log.error("Error getting remaining quota for user: {}", userId, e);
            return 0;
        }
    }

    /**
     * Checks if a user can use custom aliases based on their tier.
     *
     * @param userId The user ID to check
     * @return true if custom aliases are enabled for the user, false otherwise
     */
    public boolean canUseCustomAlias(String userId) {
        log.debug("Checking custom alias permission for user: {}", userId);
        
        try {
            SharedUserQuotaDTO userQuota = userService.getUserQuota(userId);
            
            if (userQuota == null) {
                log.warn("No quota information found for user: {}", userId);
                return false;
            }
            
            return userQuota.getCustomAliasEnabled();
            
        } catch (Exception e) {
            log.error("Error checking custom alias permission for user: {}", userId, e);
            return false;
        }
    }

    /**
     * Checks if a user can perform bulk operations based on their tier.
     *
     * @param userId The user ID to check
     * @return true if bulk operations are enabled for the user, false otherwise
     */
    public boolean canPerformBulkOperations(String userId) {
        log.debug("Checking bulk operations permission for user: {}", userId);
        
        try {
            SharedUserQuotaDTO userQuota = userService.getUserQuota(userId);
            
            if (userQuota == null) {
                log.warn("No quota information found for user: {}", userId);
                return false;
            }
            
            return userQuota.getBulkOperationsEnabled();
            
        } catch (Exception e) {
            log.error("Error checking bulk operations permission for user: {}", userId, e);
            return false;
        }
    }

    /**
     * Validates quota with external service double-check.
     * This provides an additional layer of validation by checking with the user service.
     *
     * @param userId The user ID to validate
     * @param requestedCount The number of URLs requested
     * @return true if the quota check passes, false otherwise
     */
    public boolean validateQuotaWithExternalCheck(String userId, Integer requestedCount) {
        log.debug("Performing external quota validation for user: {}, count: {}", userId, requestedCount);
        
        try {
            // Use the user service's quota check method
            return userService.checkQuotaAvailability(userId, requestedCount);
            
        } catch (Exception e) {
            log.error("Error during external quota validation for user: {}", userId, e);
            return false;
        }
    }
}
