package webserver.web.handler.response.handler;

import webserver.http.response.HttpResponse;
import webserver.web.handler.response.WebHandlerResponse;

public interface WebHandlerResponseHandler {
    boolean supports(WebHandlerResponse response);
    HttpResponse handle(WebHandlerResponse response);
}
