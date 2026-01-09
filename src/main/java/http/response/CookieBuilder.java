package http.response;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class CookieBuilder implements ResponseCookie{
    private final String name;
    private final String value;

    private String path;
    private String domain;
    private Long maxAge;          // seconds
    private ZonedDateTime expires;
    private boolean httpOnly;
    private boolean secure;
    private SameSite sameSite;

    public enum SameSite { STRICT, LAX, NONE }

    private CookieBuilder(String name, String value) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("cookie name required");
        this.name = name;
        this.value = value == null ? "" : value;
    }

    public static CookieBuilder of(String name, String value) {
        return new CookieBuilder(name, value);
    }

    public static CookieBuilder delete(String name) {
        return CookieBuilder.of(name, "")
                .path("/")         // 보통 명시
                .maxAge(0);
    }

    public CookieBuilder path(String path) {
        this.path = path;
        return this;
    }

    public CookieBuilder domain(String domain) {
        this.domain = domain;
        return this;
    }

    public CookieBuilder maxAge(long seconds) {
        if (seconds < 0) throw new IllegalArgumentException("maxAge must be >= 0");
        this.maxAge = seconds;
        return this;
    }

    public CookieBuilder maxAge(Duration duration) {
        Objects.requireNonNull(duration);
        return maxAge(duration.getSeconds());
    }

    public CookieBuilder expires(ZonedDateTime expires) {
        this.expires = expires;
        return this;
    }

    public CookieBuilder httpOnly() {
        this.httpOnly = true;
        return this;
    }

    public CookieBuilder secure() {
        this.secure = true;
        return this;
    }

    public CookieBuilder sameSite(SameSite sameSite) {
        this.sameSite = sameSite;
        return this;
    }

    @Override
    public String toHeaderValue() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=").append(value);

        if (domain != null && !domain.isBlank()) sb.append("; Domain=").append(domain);
        if (path != null && !path.isBlank()) sb.append("; Path=").append(path);

        if (maxAge != null) sb.append("; Max-Age=").append(maxAge);

        if (expires != null) {
            sb.append("; Expires=").append(DateTimeFormatter.RFC_1123_DATE_TIME.format(expires));
        }

        if (secure) sb.append("; Secure");
        if (httpOnly) sb.append("; HttpOnly");

        if (sameSite != null) {
            sb.append("; SameSite=").append(
                    switch (sameSite) {
                        case STRICT -> "Strict";
                        case LAX -> "Lax";
                        case NONE -> "None";
                    }
            );
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return toHeaderValue();
    }
}
