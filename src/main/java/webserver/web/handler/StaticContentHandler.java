package webserver.web.handler;

import webserver.http.HttpMethod;
import webserver.http.request.HttpRequest;
import webserver.web.handler.response.WebHandlerResponse;
import webserver.web.handler.response.staticcontent.StaticContentResponse;

import java.io.File;
import java.util.Arrays;

/**
 * 정
 */
public class StaticContentHandler implements WebHandler{
    private final String DEFAULT_PATH = "./src/main/resources/static";
    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public boolean checkEndpoint(HttpMethod method, String path) {
        if(!method.equals(HttpMethod.GET)) return false;
        File file1 = new File(DEFAULT_PATH + path);
        File file2 = new File(DEFAULT_PATH + path + "/index.html");
        //TODO: Prevent path traversal attack
        return (file1.exists() && file1.isFile()) || (file2.exists() && file2.isFile());
    }

    @Override
    public WebHandlerResponse handle(HttpRequest request) {
        return StaticContentResponse.of(request.getPath());
    }
}
