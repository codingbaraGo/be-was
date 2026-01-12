package web.renderer;

import http.response.HttpResponse;
import web.response.HandlerResponse;
import web.response.RedirectResponse;

public class RedirectRenderer implements HttpResponseRenderer {

    @Override
    public boolean supports(HandlerResponse response) {
        return response instanceof RedirectResponse;
    }

    @Override
    public HttpResponse handle(HttpResponse httpResponse, HandlerResponse handlerResponse) {
        RedirectResponse rr = (RedirectResponse) handlerResponse;

        httpResponse.setStatus(rr.getStatus());
        httpResponse.setHeader("Location", rr.getLocation());
        httpResponse.setHeader("Content-Length", "0");

        rr.getCookies().forEach(c -> httpResponse.addHeader("Set-Cookie", c));
        return httpResponse;
    }
}

