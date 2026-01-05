package http.response;

import http.HttpStatus;

import java.io.File;
import java.net.URLConnection;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {
    private final HttpStatus status;
    private final Map<String, List<String>> headers;
    private byte[] body;

    private HttpResponse (HttpStatus status){
        this.status = status;
        this.headers = new HashMap<>();
        setHeader("Date", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
        setHeader("Server", "be-was");
        setHeader("Connection", "close");
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
        List<String> values = headers.get(key.toLowerCase());
        return values != null ? values : new ArrayList<>();
    }

    public void addHeader(String key, String value){
        if(!headers.containsKey(key.toLowerCase()))
            headers.put(key.toLowerCase(), new ArrayList<>());
        headers.get(key.toLowerCase()).add(value);
    }

    public void setHeader(String key, String value){
        headers.put(key.toLowerCase(), new ArrayList<>());
        headers.get(key.toLowerCase()).add(value);
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
        setHeader("Content-Length", String.valueOf(body.length));
    }

    public void setBody(File file, byte[] body) {
        this.body = body;
        setHeader("Content-Type", guessContentType(file));
        setHeader("Content-Length", String.valueOf(body.length));
    }

    private String guessContentType(File file) {
        String byName = URLConnection.guessContentTypeFromName(file.getName());
        if (byName != null) return byName;

        String name = file.getName().toLowerCase();
        if (name.endsWith(".html") || name.endsWith(".htm")) return "text/html; charset=utf-8";
        if (name.endsWith(".css")) return "text/css; charset=utf-8";
        if (name.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (name.endsWith(".json")) return "application/json; charset=utf-8";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".gif")) return "image/gif";
        if (name.endsWith(".svg")) return "image/svg+xml";
        if (name.endsWith(".txt")) return "text/plain; charset=utf-8";

        return "application/octet-stream";
    }
}