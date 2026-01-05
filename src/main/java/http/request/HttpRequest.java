package http.request;

import exception.ErrorCode;
import exception.ErrorException;
import exception.ServiceException;
import http.HttpMethod;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private final HttpMethod method;
    private final Map<String, String> headers;
    private final URI uri;
    private String httpVersion;
    private String contentType;

    private Map<String, String> queryMap;

    private byte[] body;
    private InetAddress requestAddress;

    public String getHeader(String key){
        return headers.get(key.toLowerCase());
    }
    public void setHeader(String key, String value){
        headers.put(key.toLowerCase(), value);
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

    private HttpRequest (HttpMethod method,
                         String target,
                         String httpVersion) {
        this.method = method;
        this.uri = URI.create(target);
        this.httpVersion = httpVersion;
        this.headers = new HashMap<>();

    }

    public void setRequestAddress(InetAddress requestAddress) {
        this.requestAddress = requestAddress;
    }

    public InetAddress getRequestAddress() {
        return requestAddress;
    }

    public static HttpRequest from(String requestLine){
        String[] parts = requestLine.split(" ");
        try {
            return new HttpRequest(
                    HttpMethod.valueOf(parts[0].strip().toUpperCase()),
                    parts[1].strip(),
                    parts[2].strip());
        } catch (IllegalArgumentException e) {
            throw new ServiceException(ErrorCode.METHOD_NOT_ALLOWED);
        } catch (NullPointerException e){
            throw new ErrorException("Http method error");
        }
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
