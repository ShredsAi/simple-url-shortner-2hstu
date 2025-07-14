package ai.shreds.domain.ports;

import java.util.Map;

/**
 * Output port for caching URL mappings for fast lookup.
 */
public interface DomainOutputPortCacheService {

    /**
     * Cache a URL mapping for fast retrieval.
     * @param shortCode the short code/alias
     * @param originalUrl the original long URL
     */
    void cacheURLMapping(String shortCode, String originalUrl);

    /**
     * Retrieve a cached URL mapping.
     * @param shortCode the short code/alias
     * @return the original URL or null if not cached
     */
    String getCachedURL(String shortCode);

    /**
     * Invalidate a cached URL mapping.
     * @param shortCode the short code/alias to invalidate
     */
    void invalidateCache(String shortCode);

    /**
     * Bulk cache multiple URL mappings in a single operation.
     * @param mappings map of short codes to original URLs
     */
    void batchCacheURLs(Map<String, String> mappings);
}
