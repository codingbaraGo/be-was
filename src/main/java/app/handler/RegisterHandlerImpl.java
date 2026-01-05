package app.handler;

import app.db.Database;
import app.model.User;
import webserver.http.HttpMethod;
import webserver.http.request.HttpRequest;
import webserver.web.handler.DynamicViewHandler;
import webserver.web.handler.response.view.ViewResponse;

public class RegisterHandlerImpl extends DynamicViewHandler {
    public RegisterHandlerImpl() {
        super(HttpMethod.GET,
                "/create");
    }

    @Override
    public ViewResponse handle(HttpRequest request) {
        String userId = request.getQueryValue("userId");
        String password = request.getQueryValue("password");
        String name = request.getQueryValue("name");
        String email = request.getQueryValue("email");
        Database.addUser(new User(userId, password, name, email));
        return ViewResponse.of("/login");
    }
}
