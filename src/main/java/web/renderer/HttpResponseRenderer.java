package web.renderer;

import http.response.HttpResponse;
import web.response.HandlerResponse;

public interface HttpResponseRenderer {
    boolean supports(HandlerResponse response);
    HttpResponse handle(HandlerResponse response);
}
