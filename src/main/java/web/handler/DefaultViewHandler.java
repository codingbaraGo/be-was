package web.handler;

import config.VariableConfig;
import exception.ErrorException;
import http.HttpMethod;
import http.HttpStatus;
import http.request.HttpRequest;
import web.response.DynamicViewResponse;
import web.response.HandlerResponse;

import java.io.File;
import java.util.List;

public class DefaultViewHandler implements DefaultHandler{
    private final List<String> roots = VariableConfig.DYNAMIC_RESOURCE_ROOTS;
    private final HttpMethod method = HttpMethod.GET;

    @Override
    public String getPath() {
        throw new ErrorException("DynamicViewHandler::getPath should not be called");
    }

    @Override
    public HttpMethod getMethod() {
        return this.method;
    }
    @Override
    public boolean checkEndpoint(HttpMethod method, String path) {
        if(!method.equals(this.method)) return false;
        return roots.stream().anyMatch(root ->{
            File requestedFile = new File(root + path);
            String indexFilePath = path + (path.endsWith("/") ? "index.html" : "/index.html");
            File indexFile = new File(root + indexFilePath);
            return (requestedFile.exists() && requestedFile.isFile()) || (indexFile.exists() && indexFile.isFile());
        });
    }

    @Override
    public HandlerResponse handle(HttpRequest request) {
        String path = request.getPath() + (request.getPath().endsWith("/") ? "index.html" : "/index.html");
        return DynamicViewResponse.of(HttpStatus.OK, path);
    }
}
