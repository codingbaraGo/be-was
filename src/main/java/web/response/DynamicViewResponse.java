package web.response;

import exception.ErrorException;
import http.HttpStatus;
import http.request.HttpRequest;
import http.response.HttpResponse;
import web.filter.authentication.AuthenticationInfo;
import web.filter.authentication.UserAuthentication;

import java.util.HashMap;
import java.util.Map;

public class DynamicViewResponse extends HandlerResponse {
    private final String path;
    private final Map<String, Object> model;

    protected DynamicViewResponse(HttpStatus status, String path) {
        super(status);
        this.path = path;
        this.model = new HashMap<>();
    }

    public static DynamicViewResponse of (HttpStatus status, String viewPath){
        return new DynamicViewResponse(status, viewPath);
    }

    @Override
    public HandlerResponse postHandling(HttpRequest request, HttpResponse httpResponse){
        AuthenticationInfo authenticationInfo = request.getAuthenticationInfo();
        if (authenticationInfo instanceof UserAuthentication) {
            addModel("userNickname", authenticationInfo.getAttribute("nickname").orElseThrow(
                    () -> new ErrorException("DynamicViewResponse:: user nickname loading error")
            ));
            addModel("isLoginUser", true);

        } else {
            addModel("isLoginUser", false);
        }
        return this;
    }

    public String getPath() {
        return path;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void addModel(String key, Object value) {
        model.put(key, value);
    }
}
