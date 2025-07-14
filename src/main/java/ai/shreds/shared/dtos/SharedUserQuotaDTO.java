package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a user's URL quota information.
 * Contains quota limits, usage information, and feature flags.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedUserQuotaDTO {
    /** Identifier of the user. */
    private String userId;

    /** Subscription tier of the user (e.g., Free, Premium, Enterprise). */
    private String tier;

    /** Maximum number of URLs allowed. */
    private Integer urlQuotaLimit;

    /** Number of URLs already used. */
    private Integer urlQuotaUsed;

    /** Number of URLs remaining. */
    private Integer urlQuotaRemaining;

    /** Flag indicating if custom alias creation is enabled. */
    private Boolean customAliasEnabled;

    /** Flag indicating if bulk operations are enabled. */
    private Boolean bulkOperationsEnabled;

    /**
     * Explicit getter for urlQuotaRemaining to ensure availability.
     */
    public Integer getUrlQuotaRemaining() {
        return this.urlQuotaRemaining;
    }

    /**
     * Explicit getter for customAliasEnabled to ensure availability.
     */
    public Boolean getCustomAliasEnabled() {
        return this.customAliasEnabled;
    }

    /**
     * Explicit getter for bulkOperationsEnabled to ensure availability.
     */
    public Boolean getBulkOperationsEnabled() {
        return this.bulkOperationsEnabled;
    }
}