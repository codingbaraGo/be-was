package app.handler;

import http.HttpMethod;
import http.HttpStatus;
import http.request.HttpRequest;
import web.handler.SingleArgHandler;
import web.response.DynamicViewResponse;
import web.response.HandlerResponse;

public class HomeHandler extends SingleArgHandler<HttpRequest> {

    public HomeHandler() {
        super(HttpMethod.GET, "/");
    }

    @Override
    public HandlerResponse handle(HttpRequest request) {
        return DynamicViewResponse.of(HttpStatus.OK, "/index.html");
    }
}
