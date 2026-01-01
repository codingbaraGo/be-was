package webserver.http.request;

import webserver.http.HttpMethod;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private HttpMethod method;
    private String httpVersion;
    private String contentType;
    private URI uri;

    private Map<String, String> headers;
    private Map<String, String> queryMap;

    private byte[] body;
    private InetSocketAddress requestAddress;

    public String getHeader(String key){
        return headers.get(key);
    }
    public void setHeader(String key, String value){
        headers.put(key, value);
    }

    public List<String> getHeaders(){
        return headers.keySet().stream().toList();
    }

    public String getPath(){
        return uri.getPath();
    }

    public String getQuery(){
        return uri.getQuery();
    }

    public String getQueryValue(String key){
        if (queryMap == null) {
            queryMap = parseQueryToMap(uri.getQuery());
        }
        return queryMap.get(key);
    }

    public HttpMethod getMethod(){
        return this.method;
    }


    private HttpRequest (){}
    private HttpRequest (HttpMethod method,
                         String target,
                         String httpVersion) {
        this.method = method;
        this.uri = URI.create(target);
        this.httpVersion = httpVersion;
        this.headers = new HashMap<>();

    }

    public static HttpRequest from(String requestLine){
        String[] parts = requestLine.split(" ");
        return new HttpRequest(
                HttpMethod.valueOf(parts[0].strip().toUpperCase()),
                parts[1].strip(),
                parts[2].strip());
    }


    private static Map<String, String> parseQueryToMap(String queryString) {
        Map<String, String> map = new HashMap<>();
        if (queryString == null || queryString.isBlank()) {
            return map;
        }

        String[] pairs = queryString.strip().split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) continue;

            String[] kv = pair.split("=", 2); // value에 '=' 들어가도 OK
            String rawKey = kv[0];
            String rawValue = kv.length == 2 ? kv[1] : "";

            String key = urlDecode(rawKey);
            String value = urlDecode(rawValue);
            map.put(key, value);
        }
        return map;
    }

    private static String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported", e);
        }
    }
}
