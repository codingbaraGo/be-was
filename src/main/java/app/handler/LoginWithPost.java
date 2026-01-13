package app.handler;

import app.db.Database;
import app.model.User;
import config.VariableConfig;
import exception.ErrorCode;
import exception.ServiceException;
import http.HttpMethod;
import http.response.CookieBuilder;
import web.dispatch.argument.QueryParameters;
import web.handler.SingleArgHandler;
import web.response.HandlerResponse;
import web.response.RedirectResponse;
import web.session.SessionEntity;
import web.session.SessionStorage;

public class LoginWithPost extends SingleArgHandler<QueryParameters> {
    private final SessionStorage sessionManager;

    public LoginWithPost(SessionStorage sessionManager) {
        super(HttpMethod.POST, "/user/login");
        this.sessionManager = sessionManager;
    }

    @Override
    public HandlerResponse handle(QueryParameters params) {
        String email = params.getQueryValue("email")
                .orElseThrow(() -> new ServiceException(ErrorCode.LOGIN_FAILED, "email required"));

        String password = params.getQueryValue("password")
                .orElseThrow(() -> new ServiceException(ErrorCode.LOGIN_FAILED, "password required"));

        User user = Database.findUserByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorCode.LOGIN_FAILED));

        if (!user.getPassword().equals(password)) {
            throw new ServiceException(ErrorCode.LOGIN_FAILED);
        }

        SessionEntity session = sessionManager.create(
                user.getUserId(),
                user.getUserRole(),
                user.getNickname());

        RedirectResponse response = RedirectResponse.to("/");
        response.setCookie(
                CookieBuilder.of("SID", session.getId())
                        .path("/")
                        .httpOnly()
                        .sameSite(CookieBuilder.SameSite.LAX)
                        .maxAge(VariableConfig.ABSOLUTE_MS)
        );
        return response;
    }
}
