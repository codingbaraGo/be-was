package web.handler;

import http.request.HttpRequest;
import web.response.WebHandlerResponse;

public interface DefaultHandler extends WebHandler{
    WebHandlerResponse handle(HttpRequest request);
}
