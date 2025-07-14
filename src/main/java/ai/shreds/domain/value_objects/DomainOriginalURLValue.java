package ai.shreds.domain.value_objects;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public final class DomainOriginalURLValue {

    private final String value;
    private final String protocol;
    private final String domain;

    public DomainOriginalURLValue(String value) {
        if (value == null || value.isBlank() || value.length() > 2048) {
            throw new IllegalArgumentException("Original URL must be non-empty and at most 2048 characters");
        }
        try {
            URL parsed = new URL(value);
            String proto = parsed.getProtocol();
            String host = parsed.getHost();
            if (!"http".equalsIgnoreCase(proto) && !"https".equalsIgnoreCase(proto)) {
                throw new IllegalArgumentException("URL protocol must be http or https");
            }
            this.protocol = proto;
            this.domain = host;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL format: " + value, e);
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getDomain() {
        return domain;
    }

    public boolean isValid() {
        return value != null && !value.isBlank() && domain != null && !domain.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainOriginalURLValue)) return false;
        DomainOriginalURLValue that = (DomainOriginalURLValue) o;
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
