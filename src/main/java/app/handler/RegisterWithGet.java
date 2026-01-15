package app.handler;

import app.db.Database;
import app.model.User;
import exception.ErrorCode;
import exception.ServiceException;
import http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.dispatch.argument.QueryParameters;
import web.filter.authentication.UserRole;
import web.handler.SingleArgHandler;
import web.response.HandlerResponse;
import web.response.RedirectResponse;

public class RegisterWithGet extends SingleArgHandler<QueryParameters> {
    private static final Logger log = LoggerFactory.getLogger(RegisterWithGet.class);

    public RegisterWithGet() {
        super(HttpMethod.GET,
                "/create");
    }

    @Override
    public HandlerResponse handle(QueryParameters params) {
        String email = params.getQueryValue("email").orElseThrow(()-> new ServiceException(ErrorCode.MISSING_REGISTER_TOKEN, "email required"));
        String password = params.getQueryValue("password").orElseThrow(()-> new ServiceException(ErrorCode.MISSING_REGISTER_TOKEN, "password required"));
        String nickname = params.getQueryValue("nickname").orElseThrow(()-> new ServiceException(ErrorCode.MISSING_REGISTER_TOKEN, "nickname required"));
        Database.addUser(new User(password, nickname, email, UserRole.MEMBER.toString()));
        log.info("Registered - password:{}, nickname:{}, email:{}", password, nickname, email);
        return RedirectResponse.to("/login");
    }
}
