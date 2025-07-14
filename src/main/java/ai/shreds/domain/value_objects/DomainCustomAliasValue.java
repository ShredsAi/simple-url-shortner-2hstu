package ai.shreds.domain.value_objects;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class DomainCustomAliasValue {
    private static final Pattern FORMAT_PATTERN = Pattern.compile("^(?![-_])[A-Za-z0-9_-]{4,32}(?<![-_])$");

    private final String value;

    public DomainCustomAliasValue(String value) {
        if (value == null || !FORMAT_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                "Alias must be 4-32 characters, alphanumeric, hyphens/underscores allowed, cannot start or end with hyphen/underscore");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isValid() {
        return FORMAT_PATTERN.matcher(value).matches();
    }

    public boolean matchesReservedPattern(List<String> patterns) {
        if (patterns == null) {
            return false;
        }
        for (String p : patterns) {
            if (value.equalsIgnoreCase(p) || value.matches(p)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainCustomAliasValue)) return false;
        DomainCustomAliasValue that = (DomainCustomAliasValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
