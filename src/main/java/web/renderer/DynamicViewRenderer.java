package web.renderer;

import http.response.HttpResponse;
import web.response.DynamicViewResponse;
import web.response.HandlerResponse;
import web.renderer.view.TemplateEngine;

import java.nio.charset.StandardCharsets;

public class DynamicViewRenderer implements HttpResponseRenderer {
    private final TemplateEngine templateEngine;

    public DynamicViewRenderer(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public boolean supports(HandlerResponse response) {
        return response instanceof DynamicViewResponse;
    }

    @Override
    public HttpResponse handle(HttpResponse httpResponse, HandlerResponse handlerResponse) {
        DynamicViewResponse dynamicViewResponse = (DynamicViewResponse) handlerResponse;
        String path = dynamicViewResponse.getPath();
        templateEngine.clearCache();
        String render = templateEngine.render(path, dynamicViewResponse.getModel());

        httpResponse.setStatus(handlerResponse.getStatus());
        httpResponse.setBody(render.getBytes(StandardCharsets.UTF_8));
        httpResponse.addHeader("Content-Type", "text/html; charset=utf-8");
        handlerResponse.getCookies().forEach(cookie->httpResponse.addHeader("Set-Cookie", cookie));
        return httpResponse;
    }
}
