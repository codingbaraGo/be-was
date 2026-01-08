package app.handler;

import app.db.Database;
import app.model.User;
import exception.ErrorCode;
import exception.ServiceException;
import http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.dispatch.argument.QueryParameters;
import web.handler.SingleArgHandler;
import web.response.HandlerResponse;
import web.response.StaticViewResponse;

public class RegisterWithGet extends SingleArgHandler<QueryParameters> {
    private static final Logger log = LoggerFactory.getLogger(RegisterWithGet.class);

    public RegisterWithGet() {
        super(HttpMethod.GET,
                "/create");
    }

    @Override
    public HandlerResponse handle(QueryParameters params) {
        String userId = params.getQueryValue("userId").orElseThrow(()-> new ServiceException(ErrorCode.MISSING_REGISTER_TOKEN, "userId required"));
        String password = params.getQueryValue("password").orElseThrow(()-> new ServiceException(ErrorCode.MISSING_REGISTER_TOKEN, "password required"));
        String name = params.getQueryValue("name").orElseThrow(()-> new ServiceException(ErrorCode.MISSING_REGISTER_TOKEN, "name required"));
        String email = params.getQueryValue("email").orElseThrow(()-> new ServiceException(ErrorCode.MISSING_REGISTER_TOKEN, "email required"));
        Database.addUser(new User(userId, password, name, email));
        log.info("Registered - userId:{}, password:{}, name:{}, email:{}", userId, password, name, email);
        return StaticViewResponse.of("/login");
    }
}
