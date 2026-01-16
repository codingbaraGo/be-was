package web.handler;

import config.DatabaseConfig;
import config.VariableConfig;
import exception.ErrorException;
import http.HttpMethod;
import http.request.HttpRequest;
import web.response.HandlerResponse;
import web.response.StaticViewResponse;

import java.io.File;
import java.util.List;

public class UserProfileImageHandler implements DefaultHandler {
    private final HttpMethod method = HttpMethod.GET;

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
        if(!path.startsWith("/static/mypage/img/")) return false;
        return true;
    }

    @Override
    public HandlerResponse handle(HttpRequest request) {
        File requestedFile = new File(DatabaseConfig.PROFILE_IMG_DIR + request.getPath());
        if (requestedFile.exists() && requestedFile.isFile()){
            return StaticViewResponse.of(request.getPath());
        }
        return StaticViewResponse.of("/img/basic_profileImage.svg");
    }
}
