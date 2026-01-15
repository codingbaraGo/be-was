package app.handler;

import app.db.UserRepository;
import app.model.User;
import config.VariableConfig;
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

public class RegisterWithPost extends SingleArgHandler<QueryParameters> {
    private static final String EMAIL = "email";
    private static final String NICKNAME = "nickname";
    private static final String PASSWORD = "password";

    private static final Logger log = LoggerFactory.getLogger(RegisterWithPost.class);

    private final UserRepository userRepository;

    public RegisterWithPost(UserRepository userRepository) {
        super(HttpMethod.POST, "/user/create");
        this.userRepository = userRepository;
    }

    @Override
    public HandlerResponse handle(QueryParameters params) {
        String email = getRequired(params, EMAIL);
        String nickname = getRequired(params, NICKNAME);
        String password = getRequired(params, PASSWORD);

        validate(email, nickname, password);

        User saved = userRepository.save(
                new User(password, nickname, email, UserRole.MEMBER.toString()));

        log.info("Registered id:{}, email:{}, nickname:{}, password:{}",
                saved.getId(), email, nickname, password);
        return RedirectResponse.to("/login");
    }

    private void validate(String email, String nickname, String password) {
        validateDuplicate(email, nickname);
        validateLength(email, VariableConfig.EMAIL_MIN, VariableConfig.EMAIL_MAX, ErrorCode.EMAIL_LENGTH_INVALID);
        validateLength(nickname, VariableConfig.NICKNAME_MIN, VariableConfig.NICKNAME_MAX, ErrorCode.NICKNAME_LENGTH_INVALID);
        validateLength(password, VariableConfig.PASSWORD_MIN, VariableConfig.PASSWORD_MAX, ErrorCode.PASSWORD_LENGTH_INVALID);
    }

    private String getRequired(QueryParameters params, String key) {
        return params.getQueryValue(key)
                .orElseThrow(() -> new ServiceException(ErrorCode.MISSING_REGISTER_TOKEN, key + " required"));
    }

    private void validateLength(String value, int min, int max, ErrorCode code) {
        int len = value.length();
        if (len < min || len > max)
            throw new ServiceException(code);
    }

    private void validateDuplicate(String email, String nickname) {
        if (!userRepository.findByColumn(EMAIL, email).isEmpty())
            throw new ServiceException(ErrorCode.EMAIL_ALREADY_EXISTS);

        if (!userRepository.findByColumn(NICKNAME, nickname).isEmpty())
            throw new ServiceException(ErrorCode.NICKNAME_ALREADY_EXISTS);
    }
}
