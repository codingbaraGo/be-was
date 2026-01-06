package web.dispatch.argument;

import http.request.HttpRequest;
import web.dispatch.ArgumentResolver;

public class HttpRequestResolver extends ArgumentResolver<HttpRequest> {

    @Override
    public HttpRequest resolve(HttpRequest request) {
        return request;
    }
}
