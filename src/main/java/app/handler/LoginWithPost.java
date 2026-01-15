package app.handler;

import app.db.Database;
import app.db.UserRepository;
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

import java.util.List;

public class LoginWithPost extends SingleArgHandler<QueryParameters> {
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";


    private final SessionStorage sessionManager;
    private final UserRepository userRepository;

    public LoginWithPost(SessionStorage sessionManager, UserRepository userRepository) {
        super(HttpMethod.POST, "/user/login");
        this.sessionManager = sessionManager;
        this.userRepository = userRepository;
    }

    @Override
    public HandlerResponse handle(QueryParameters params) {
        String email = getRequired(params, EMAIL);
        String password = getRequired(params, PASSWORD);

        List<User> userList = userRepository.findByColumn(EMAIL, email);
        if(userList.isEmpty())
            throw new ServiceException(ErrorCode.EMAIL_NOT_FOUND, "회원가입을 하시겠습니까?");

        User user = userList.get(0);
        if (!user.getPassword().equals(password)) {
            throw new ServiceException(ErrorCode.LOGIN_FAILED);
        }

        SessionEntity session = sessionManager.create(
                user.getId(),
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

    private String getRequired(QueryParameters params, String key) {
        return params.getQueryValue(key)
                .orElseThrow(() -> new ServiceException(ErrorCode.MISSING_REGISTER_TOKEN, key + " required"));
    }
}
