package web.handler;

import http.HttpMethod;
import web.response.HandlerResponse;

public abstract class SingleArgHandler<T> implements WebHandler{
    protected final HttpMethod method;
    protected final String path;

    protected SingleArgHandler(HttpMethod method, String path) {
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

    public abstract HandlerResponse handle(T arg);
}
