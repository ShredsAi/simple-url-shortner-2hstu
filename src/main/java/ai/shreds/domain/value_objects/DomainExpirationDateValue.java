package ai.shreds.domain.value_objects;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Value object representing an expiration date for a URL.
 * Encapsulates expiration logic and remaining time calculations.
 */
public final class DomainExpirationDateValue {

    private final Date expirationDate;

    public DomainExpirationDateValue(Date expirationDate) {
        if (expirationDate == null) {
            throw new IllegalArgumentException("Expiration date cannot be null");
        }
        this.expirationDate = new Date(expirationDate.getTime());
    }

    public Date getExpirationDate() {
        return new Date(expirationDate.getTime());
    }

    public boolean isExpired() {
        return new Date().after(expirationDate);
    }

    public Integer getRemainingDays() {
        long remainingTime = expirationDate.getTime() - System.currentTimeMillis();
        if (remainingTime <= 0) {
            return 0;
        }
        return (int) TimeUnit.MILLISECONDS.toDays(remainingTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainExpirationDateValue)) return false;
        DomainExpirationDateValue that = (DomainExpirationDateValue) o;
        return Objects.equals(expirationDate, that.expirationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expirationDate);
    }

    @Override
    public String toString() {
        return expirationDate.toString();
    }
}