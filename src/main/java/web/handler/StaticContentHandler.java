package web.handler;

import config.VariableConfig;
import exception.ErrorException;
import http.HttpMethod;
import http.request.HttpRequest;
import web.response.WebHandlerResponse;
import web.response.StaticContentResponse;

import java.io.File;
import java.util.List;

public class StaticContentHandler implements DefaultHandler {
    private final List<String> staticResourceRoots = VariableConfig.STATIC_RESOURCE_ROOTS;
    private final HttpMethod method = HttpMethod.GET;

    public StaticContentHandler() {}

    @Override
    public String getPath() {
        throw new ErrorException("StaticContentHandler::getPath should not be called");
    }

    @Override
    public HttpMethod getMethod() {
        return this.method;
    }

    @Override
    public boolean checkEndpoint(HttpMethod method, String path) {
        if(!method.equals(this.method)) return false;
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
