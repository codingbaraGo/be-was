package web.posthandler;

import http.response.HttpResponse;
import web.response.WebHandlerResponse;

public interface WebHandlerResponseHandler {
    boolean supports(WebHandlerResponse response);
    HttpResponse handle(WebHandlerResponse response);
}
