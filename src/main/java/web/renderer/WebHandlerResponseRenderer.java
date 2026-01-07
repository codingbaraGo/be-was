package web.renderer;

import http.response.HttpResponse;
import web.response.HandlerResponse;

public interface WebHandlerResponseRenderer {
    boolean supports(HandlerResponse response);
    HttpResponse handle(HandlerResponse response);
}
