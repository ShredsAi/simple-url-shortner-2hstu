package ai.shreds.domain.value_objects;

import java.util.Objects;

/**
 * Value object representing a security score for a URL.
 * Encapsulates the security rating from 0-100 and provides validation logic.
 */
public final class DomainSecurityScoreValue {
    
    /* The security score value between 0-100 */
    private final Integer score;
    
    /**
     * Creates a new security score with the specified value.
     * 
     * @param score The security score value (0-100), must be in valid range
     * @throws IllegalArgumentException if the score is null or outside the valid range
     */
    public DomainSecurityScoreValue(Integer score) {
        if (score == null) {
            throw new IllegalArgumentException("Security score cannot be null");
        }
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Security score must be between 0 and 100");
        }
        this.score = score;
    }
    
    /**
     * Gets the security score value.
     * 
     * @return The security score value as an Integer
     */
    public Integer getScore() {
        return score;
    }
    
    /**
     * Checks if the security score is acceptable based on a threshold.
     * 
     * @param threshold The minimum acceptable score threshold
     * @return true if the score is greater than or equal to the threshold
     */
    public boolean isAcceptable(Integer threshold) {
        if (threshold == null) {
            return true; // If no threshold is provided, consider it acceptable
        }
        return score >= threshold;
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        DomainSecurityScoreValue that = (DomainSecurityScoreValue) other;
        return Objects.equals(score, that.score);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(score);
    }
    
    @Override
    public String toString() {
        return score.toString();
    }
}