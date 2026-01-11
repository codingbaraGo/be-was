package http.request;

import exception.ErrorCode;
import exception.ErrorException;
import exception.ServiceException;
import http.HttpMethod;

import java.net.InetAddress;
import java.net.URI;
import java.util.*;

public class HttpRequest {
    private final HttpMethod method;
    private final Map<String, String> headers; //TODO: Map<String, List<String>> 타입으로 변경
    private Cookies cookies;
    private String httpVersion;
    private final URI uri;
    private String contentType;
    private byte[] body;
    private UUID rid;

    private InetAddress requestAddress;

    private HttpRequest (HttpMethod method,
                         String target,
                         String httpVersion) {
        this.method = method;
        this.uri = URI.create(target);
        this.httpVersion = httpVersion;
        this.headers = new HashMap<>();
        this.cookies = Cookies.empty();
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

    public byte[] getBody() {
        return body;
    }
    public String getContentType() {
        return contentType;
    }

    public void setHeader(String key, String value){
        headers.put(key.toLowerCase(), value);
        if (key.equalsIgnoreCase("Content-Type")) {
            this.contentType = value;
        } else if (key.equalsIgnoreCase("Cookie")) {
            this.cookies = Cookies.parse(value);
        }
    }

    public String getHeader(String key){
        return headers.get(key.toLowerCase());
    }

    public List<String> getHeaders(){
        return headers.keySet().stream().toList();
    }

    public Cookies getCookies(){
        return this.cookies;
    }

    public Optional<String> getCookieValue(String key){
        return this.cookies.get(key);
    }

    public String getPath(){
        return uri.getPath();
    }

    public String getQueryString(){
        return uri.getQuery();
    }

    public HttpMethod getMethod(){
        return this.method;
    }

    public void setRequestAddress(InetAddress requestAddress) {
        this.requestAddress = requestAddress;
    }

    public InetAddress getRequestAddress() {
        return requestAddress;
    }

    public void setBody(byte[] body){
        this.body = body;
    }

    public String getOrGenerateRid(){
        if(this.rid == null)
            this.rid = UUID.randomUUID();
        return this.rid.toString();
    }

    public UUID getRid() {
        return rid;
    }
}
