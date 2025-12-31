package webserver.web.handler;

import webserver.http.HttpMethod;
import webserver.http.request.HttpRequest;
import webserver.web.handler.response.WebHandlerResponse;
import webserver.web.handler.response.staticcontent.StaticContentResponse;

import java.io.File;

/**
 * 정
 */
public class StaticContentHandler implements WebHandler{
    private final String DEFAULT_PATH = "/src/main/java/resource";
    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public boolean checkEndpoint(HttpMethod method, String path) {
        if(!method.equals(HttpMethod.GET)) return false;
        File file = new File(DEFAULT_PATH + path);
        return file.exists() && file.isFile();
    }

    @Override
    public WebHandlerResponse handle(HttpRequest request) {
        return StaticContentResponse.of(request.getPath());
    }
}
