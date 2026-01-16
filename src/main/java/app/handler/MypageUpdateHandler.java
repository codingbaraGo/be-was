package app.handler;

import app.db.UserRepository;
import app.model.User;
import config.DatabaseConfig;
import exception.ErrorCode;
import exception.ErrorException;
import exception.ServiceException;
import http.HttpMethod;
import http.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.dispatch.argument.MultipartFile;
import web.dispatch.argument.MultipartForm;
import web.filter.authentication.AuthenticationInfo;
import web.handler.DoubleArgHandler;
import web.response.HandlerResponse;
import web.response.RedirectResponse;
import web.session.SessionEntity;
import web.session.SessionStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static config.DatabaseConfig.PROFILE_IMG_DIR;
import static config.VariableConfig.*;
import static exception.ErrorCode.*;

public class MypageUpdateHandler extends DoubleArgHandler<MultipartForm, HttpRequest> {
    private static final Logger log = LoggerFactory.getLogger(MypageUpdateHandler.class);
    private final UserRepository userRepository;
    private final SessionStorage sessionStorage;

    public MypageUpdateHandler(UserRepository userRepository, SessionStorage sessionStorage) {
        super(HttpMethod.POST, "/mypage/update");
        this.userRepository = userRepository;
        this.sessionStorage = sessionStorage;
    }

    @Override
    public HandlerResponse handle(MultipartForm form, HttpRequest request) {
        AuthenticationInfo info = request.getAuthenticationInfo();
        Long userId = info.getUserId().orElseThrow(
                () -> new ErrorException("user id should exists at /mypage"));
        MultipartFile file = form.getFile("profileImage")
                .orElseThrow(
                        ()-> new ServiceException(INVALID_INPUT, "사진이 포함되어야 합니다."));
        if(file!=null && file.bytes().length>0){
            Path filePath = Paths.get(PROFILE_IMG_DIR)
                    .resolve(userId.toString());
            try {
                log.debug("write file to {}", filePath);
                Files.write(filePath,
                        file.bytes(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e){
                log.error(e.fillInStackTrace().toString());
                throw new ErrorException("error");
            }
        } else{
            try {
                Files.deleteIfExists(Paths.get(PROFILE_IMG_DIR)
                        .resolve(userId.toString()));
            } catch (IOException e) {
                log.error(e.getStackTrace().toString());
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new ErrorException("MypageUpdateHandlerError"));

        if(form.getField("nickname").isPresent()
                && !form.getField("nickname").get().isBlank()){
            String nickname = form.getField("nickname").get();
            validateLength(nickname, NICKNAME_MIN, NICKNAME_MAX, NICKNAME_LENGTH_INVALID);
            user.setNickname(nickname);
            SessionEntity sessionEntity = sessionStorage.getValid(
                    request.getCookieValue("SID")
                            .orElseThrow(
                                    () -> new ErrorException("mypage update error")));
            sessionEntity.setNickname(nickname);
        }
        if (form.getField("password").isPresent()
                && form.getField("passwordConfirm").isPresent()
                && !form.getField("password").get().isBlank()) {
            String password = form.getField("password").get();
            validateLength(password, PASSWORD_MIN, PASSWORD_MAX, PASSWORD_LENGTH_INVALID);
            if (password.equals(form.getField("passwordConfirm").get())) {
                user.setPassword(password);
            } else {
                throw new ServiceException(PASSWORD_DOUBLE_CHECK_FAIL);
            }
        }

        userRepository.update(user);

        return RedirectResponse.to("/mypage");
    }

    private void validateLength(String value, int min, int max, ErrorCode code) {
        int len = value.length();
        if (len < min || len > max)
            throw new ServiceException(code);
    }

    private static String extractExtension(MultipartFile multipartFile) {
        return switch(multipartFile.contentType().toLowerCase()){
            case "image/png" -> ".png";
            case "image/jpg" -> ".jpg";
            case "image/jpeg" -> ".jpeg";
            default -> throw new ServiceException(UNSUPPORTED_IMAGE_TYPE);
        };
    }
}
