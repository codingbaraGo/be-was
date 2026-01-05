package web.handler;

import config.VariableConfig;
import http.HttpMethod;
import http.request.HttpRequest;
import web.response.WebHandlerResponse;
import web.response.StaticContentResponse;

import java.io.File;
import java.util.List;

/**
 * 정
 */
public class StaticContentHandler implements WebHandler{
    private final List<String> staticResourceRoots = VariableConfig.STATIC_RESOURCE_ROOTS;
    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public boolean checkEndpoint(HttpMethod method, String path) {
        if(!method.equals(HttpMethod.GET)) return false;
        return staticResourceRoots.stream().anyMatch(root ->{
            File requestedFile = new File(root + path);
            String indexFilePath = path + (path.endsWith("/") ? "index.html" : "/index.html");
            File indexFile = new File(root + indexFilePath);
            return (requestedFile.exists() && requestedFile.isFile()) || (indexFile.exists() && indexFile.isFile());
        });
    }

    @Override
    public WebHandlerResponse handle(HttpRequest request) {
        return StaticContentResponse.of(request.getPath());
    }
}
