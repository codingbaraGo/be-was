package web.handler;

import http.HttpMethod;

public interface WebHandler {
    HttpMethod getMethod();
    String getPath();
    boolean checkEndpoint(HttpMethod method, String path);
}
