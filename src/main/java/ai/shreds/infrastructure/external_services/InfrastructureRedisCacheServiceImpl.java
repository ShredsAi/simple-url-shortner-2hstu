package ai.shreds.infrastructure.external_services;

import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ai.shreds.domain.ports.DomainOutputPortCacheService;

@Service
public class InfrastructureRedisCacheServiceImpl implements DomainOutputPortCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final Duration defaultTtl;

    public InfrastructureRedisCacheServiceImpl(RedisTemplate<String, String> redisTemplate,
                                               @Value("${url.cache.ttl:86400}") long ttlSeconds) {
        this.redisTemplate = redisTemplate;
        this.defaultTtl = Duration.ofSeconds(ttlSeconds);
    }

    private String buildCacheKey(String shortCode) {
        return "url:cache:" + shortCode;
    }

    @Override
    public void cacheURLMapping(String shortCode, String originalUrl) {
        redisTemplate.opsForValue().set(buildCacheKey(shortCode), originalUrl, defaultTtl);
    }

    @Override
    public String getCachedURL(String shortCode) {
        return redisTemplate.opsForValue().get(buildCacheKey(shortCode));
    }

    @Override
    public void invalidateCache(String shortCode) {
        redisTemplate.delete(buildCacheKey(shortCode));
    }

    @Override
    public void batchCacheURLs(Map<String, String> mappings) {
        redisTemplate.executePipelined(connection -> {
            mappings.forEach((code, url) -> {
                byte[] key = redisTemplate.getKeySerializer().serialize(buildCacheKey(code));
                byte[] value = redisTemplate.getValueSerializer().serialize(url);
                connection.setEx(key, defaultTtl.getSeconds(), value);
            });
            return null;
        });
    }
}
