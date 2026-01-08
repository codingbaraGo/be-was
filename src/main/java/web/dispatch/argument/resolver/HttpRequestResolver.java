package web.dispatch.argument.resolver;

import http.request.HttpRequest;
import web.dispatch.argument.ArgumentResolver;

public class HttpRequestResolver extends ArgumentResolver<HttpRequest> {

    @Override
    public HttpRequest resolve(HttpRequest request) {
        return request;
    }
}
