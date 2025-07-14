package ai.shreds.infrastructure.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for retrying operations with exponential backoff.
 */
@Slf4j
@Component
public class InfrastructureRetryUtil {

    private final int maxRetries;
    private final long initialDelay;
    private final long maxDelay;

    public InfrastructureRetryUtil(
            @Value("${retry.max-retries:3}") int maxRetries,
            @Value("${retry.initial-delay:1000}") long initialDelay,
            @Value("${retry.max-delay:10000}") long maxDelay) {
        this.maxRetries = maxRetries;
        this.initialDelay = initialDelay;
        this.maxDelay = maxDelay;
    }

    /**
     * Execute an operation with retry logic. If the operation fails, it will be retried
     * up to the configured number of times with exponential backoff.
     *
     * @param action The operation to execute
     * @param <T>    The return type of the operation
     * @return The result of the operation
     * @throws RuntimeException if the operation fails after all retries
     */
    public <T> T executeWithRetry(Supplier<T> action) {
        int attempts = 0;
        RuntimeException lastException = null;

        while (attempts < maxRetries) {
            try {
                return action.get();
            } catch (Exception e) {
                attempts++;
                lastException = e instanceof RuntimeException ? (RuntimeException) e 
                    : new RuntimeException(e);
                
                if (attempts < maxRetries) {
                    long delay = calculateBackoff(attempts);
                    log.warn("Attempt #{} failed, retrying in {} ms: {}", 
                        attempts, delay, e.getMessage());
                    
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
            }
        }

        log.error("Operation failed after {} attempts", maxRetries);
        throw lastException;
    }

    /**
     * Execute an asynchronous operation with retry logic.
     *
     * @param action The asynchronous operation to execute
     * @param <T>    The return type of the operation
     * @return A CompletableFuture that will complete with the result of the operation
     */
    public <T> CompletableFuture<T> executeWithRetryAsync(Supplier<CompletableFuture<T>> action) {
        CompletableFuture<T> future = new CompletableFuture<>();
        executeWithRetryAsync(action, 0, future);
        return future;
    }

    private <T> void executeWithRetryAsync(Supplier<CompletableFuture<T>> action, int attempt, 
                                         CompletableFuture<T> result) {
        action.get().handle((response, error) -> {
            if (error == null) {
                result.complete(response);
            } else if (attempt < maxRetries - 1) {
                long delay = calculateBackoff(attempt + 1);
                log.warn("Async attempt #{} failed, retrying in {} ms: {}", 
                    attempt + 1, delay, error.getMessage());
                
                CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS)
                    .execute(() -> executeWithRetryAsync(action, attempt + 1, result));
            } else {
                log.error("Async operation failed after {} attempts", maxRetries);
                result.completeExceptionally(error);
            }
            return null;
        });
    }

    /**
     * Calculate the backoff delay for the given attempt number using exponential backoff
     * with jitter to prevent thundering herd problem.
     *
     * @param attempt The attempt number (starting from 1)
     * @return The delay in milliseconds
     */
    private long calculateBackoff(int attempt) {
        long exponentialBackoff = initialDelay * (long) Math.pow(2, attempt - 1);
        long cappedBackoff = Math.min(exponentialBackoff, maxDelay);
        // Add jitter (±20%)
        double jitterFactor = 0.8 + (Math.random() * 0.4);
        return (long) (cappedBackoff * jitterFactor);
    }
}