package webserver.web.handler;

import http.HttpMethod;
import http.request.HttpRequest;
import webserver.web.handler.response.view.ViewResponse;

public abstract class DynamicViewHandler implements WebHandler{
    protected final HttpMethod method;
    protected final String path;

    protected DynamicViewHandler(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public boolean checkEndpoint(HttpMethod method, String path) {
        return this.method.equals(method) && this.path.equals(path);
    }

    public abstract ViewResponse handle(HttpRequest request);
}
