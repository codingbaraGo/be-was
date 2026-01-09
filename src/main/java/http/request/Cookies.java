package http.request;

import java.util.*;

public class Cookies {
    private final Map<String, String> values;
    private Cookies(Map<String, String> values) {
        this.values = Collections.unmodifiableMap(values);
    }

    public static Cookies empty() {
        return new Cookies(new HashMap<>());
    }

    //TODO: XSS 방어 코드 추가
    public static Cookies parse(String rawHeader) {
        if (rawHeader == null || rawHeader.isBlank()) return empty();

        Map<String, String> map = new LinkedHashMap<>();
        String[] parts = rawHeader.split(";");
        for (String part : parts) {
            String token = part.trim();
            if (token.isEmpty()) continue;

            String[] kv = token.split("=", 2);
            String name = kv[0].trim();
            String value = kv.length == 2 ? kv[1].trim() : "";

            if (!name.isEmpty()) {
                map.put(name, value);
            }
        }
        return new Cookies(map);
    }

    public Optional<String> get(String name) {
        return Optional.ofNullable(values.get(name));
    }

    public boolean contains(String name) {
        return values.containsKey(name);
    }

    public Map<String, String> asMap() {
        return values;
    }
}
