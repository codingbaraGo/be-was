package webserver.http.response;

import webserver.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {
    private final HttpStatus status;
    private Map<String, List<String>> headers;
    private byte[] body;

    private HttpResponse (HttpStatus status){
        this.status = status;
        this.headers = new HashMap<>();
    }

    public static HttpResponse of (HttpStatus status){
        return new HttpResponse(status);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public List<String> getHeaders() {
        return headers.keySet().stream().toList();
    }

    public List<String> getHeader(String key){
        List<String> values = headers.get(key);
        return values != null ? values : new ArrayList<>();
    }

    public void addHeader(String key, String value){
        if(!headers.containsKey(key))
            headers.put(key, new ArrayList<>());
        headers.get(key).add(value);
    }

    public void setHeader(String key, String value){
        headers.put(key, new ArrayList<>());
        headers.get(key).add(value);
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}