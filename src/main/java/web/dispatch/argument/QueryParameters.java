package web.dispatch.argument;

import exception.ErrorCode;
import exception.ServiceException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class QueryParameters {
    private final Map<String, List<String>> params;
    private String queryString;

    private QueryParameters(String queryString) {
        this.params = parseQueryToMap(queryString);
        this.queryString = queryString;
    }

    public static QueryParameters of(String queryString) {
        return new QueryParameters(queryString);
    }

    public String getQueryString() {
        return queryString;
    }

    public Optional<String> getQueryValue(String key){
        if(params.containsKey(key))
            return Optional.of(params.get(key).get(0));
        return Optional.empty();
    }

    public String getValidQueryValue(String key){
        return getQueryValue(key)
                .orElseThrow(() -> new ServiceException(ErrorCode.MISSING_PARAMETER, key + " required"));
    }

    public List<String> getQueryValues(String key){
        if(params.containsKey(key))
            return params.get(key);
        return List.of();
    }

    public List<String> getQueryKeys(){
        return params.keySet().stream().toList();
    }

    private Map<String, List<String>> parseQueryToMap(String queryString) {
        Map<String, List<String>> map = new HashMap<>();
        if (queryString == null || queryString.isBlank()) {
            return map;
        }

        String[] pairs = queryString.strip().split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) continue;

            String[] kv = pair.split("=", 2);
            String rawKey = kv[0];
            String rawValue = kv.length == 2 ? kv[1] : "";

            String key = urlDecode(rawKey);
            String value = urlDecode(rawValue);
            if(!map.containsKey(key)) map.put(key, new ArrayList<>());
            map.get(key).add(value);
        }
        return map;
    }

    private String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported", e);
        }
    }
}
