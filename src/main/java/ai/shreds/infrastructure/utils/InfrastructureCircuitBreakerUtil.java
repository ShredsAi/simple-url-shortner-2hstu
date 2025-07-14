package ai.shreds.infrastructure.utils;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InfrastructureCircuitBreakerUtil {

    private final AtomicReference<CircuitBreakerState> state = new AtomicReference<>(CircuitBreakerState.CLOSED);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicReference<Instant> lastFailureTime = new AtomicReference<>(Instant.now());
    private final AtomicInteger halfOpenSuccessCount = new AtomicInteger(0);

    private final int failureThreshold;
    private final long resetTimeoutMs;
    private final int halfOpenRequests;

    public InfrastructureCircuitBreakerUtil(
            @Value("${circuit-breaker.failure-threshold:5}") int failureThreshold,
            @Value("${circuit-breaker.reset-timeout:60000}") long resetTimeoutMs,
            @Value("${circuit-breaker.half-open-requests:3}") int halfOpenRequests) {
        this.failureThreshold = failureThreshold;
        this.resetTimeoutMs = resetTimeoutMs;
        this.halfOpenRequests = halfOpenRequests;
    }

    public <T> T execute(Supplier<T> action) {
        CircuitBreakerState currentState = state.get();

        switch (currentState) {
            case OPEN:
                if (shouldAttemptReset()) {
                    return attemptHalfOpen(action);
                }
                throw new RuntimeException("Circuit breaker is OPEN");

            case HALF_OPEN:
                return handleHalfOpen(action);

            case CLOSED:
            default:
                return handleClosed(action);
        }
    }

    private <T> T handleClosed(Supplier<T> action) {
        try {
            T result = action.get();
            resetFailureCount();
            return result;
        } catch (Exception e) {
            handleFailure();
            throw e;
        }
    }

    private <T> T handleHalfOpen(Supplier<T> action) {
        try {
            T result = action.get();
            halfOpenSuccessCount.incrementAndGet();
            if (halfOpenSuccessCount.get() >= halfOpenRequests) {
                transitionToClosed();
            }
            return result;
        } catch (Exception e) {
            transitionToOpen();
            throw e;
        }
    }

    private <T> T attemptHalfOpen(Supplier<T> action) {
        state.set(CircuitBreakerState.HALF_OPEN);
        halfOpenSuccessCount.set(0);
        return handleHalfOpen(action);
    }

    private void handleFailure() {
        int currentFailures = failureCount.incrementAndGet();
        lastFailureTime.set(Instant.now());

        if (currentFailures >= failureThreshold) {
            transitionToOpen();
        }
    }

    private boolean shouldAttemptReset() {
        return Instant.now().isAfter(lastFailureTime.get().plusMillis(resetTimeoutMs));
    }

    private void transitionToOpen() {
        state.set(CircuitBreakerState.OPEN);
        lastFailureTime.set(Instant.now());
    }

    private void transitionToClosed() {
        state.set(CircuitBreakerState.CLOSED);
        resetFailureCount();
    }

    private void resetFailureCount() {
        failureCount.set(0);
    }

    public CircuitBreakerState getState() {
        return state.get();
    }

    public enum CircuitBreakerState {
        CLOSED,
        OPEN,
        HALF_OPEN
    }
}