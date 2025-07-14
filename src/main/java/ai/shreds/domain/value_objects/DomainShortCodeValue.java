package ai.shreds.domain.value_objects;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing a URL short code.
 * Encapsulates validation logic and generation timestamp.
 */
public final class DomainShortCodeValue {
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Za-z0-9]{5,10}$");

    private final String code;
    private final Date generationTimestamp;

    public DomainShortCodeValue(String code) {
        this(code, new Date());
    }

    public DomainShortCodeValue(String code, Date generationTimestamp) {
        if (code == null || !CODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException("Short code must be 5-10 alphanumeric characters");
        }
        if (generationTimestamp == null) {
            throw new IllegalArgumentException("Generation timestamp cannot be null");
        }
        this.code = code;
        this.generationTimestamp = new Date(generationTimestamp.getTime());
    }

    public String getCode() {
        return code;
    }

    public Date getGenerationTimestamp() {
        return new Date(generationTimestamp.getTime());
    }

    public boolean validate() {
        return CODE_PATTERN.matcher(code).matches();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainShortCodeValue)) return false;
        DomainShortCodeValue that = (DomainShortCodeValue) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code;
    }
}