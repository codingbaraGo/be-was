package app.handler;

import app.db.Database;
import app.model.User;
import exception.ErrorCode;
import exception.ServiceException;
import http.HttpMethod;
import http.request.HttpRequest;
import web.handler.SingleArgHandler;
import web.response.HandlerResponse;
import web.response.StaticViewResponse;

public class RegisterHandlerImpl extends SingleArgHandler<HttpRequest> {
    public RegisterHandlerImpl() {
        super(HttpMethod.GET,
                "/create");
    }

    @Override
    public HandlerResponse handle(HttpRequest request) {
        String userId = request.getQueryValue("userId").orElseThrow(()-> new ServiceException(ErrorCode.MISSING_REGISTER_TOKEN, "userId required"));
        String password = request.getQueryValue("password").orElseThrow(()-> new ServiceException(ErrorCode.MISSING_REGISTER_TOKEN, "password required"));
        String name = request.getQueryValue("name").orElseThrow(()-> new ServiceException(ErrorCode.MISSING_REGISTER_TOKEN, "name required"));
        String email = request.getQueryValue("email").orElseThrow(()-> new ServiceException(ErrorCode.MISSING_REGISTER_TOKEN, "email required"));
        Database.addUser(new User(userId, password, name, email));
        return StaticViewResponse.of("/login");
    }
}
