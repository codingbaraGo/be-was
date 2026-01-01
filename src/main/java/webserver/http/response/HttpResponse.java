package webserver.http.response;

import webserver.http.HttpMethod;

import java.util.List;
import java.util.Map;

public class HttpResponse {
    private HttpMethod method;
    private Map<String, List<String>> headers;
    private byte[] body;
}