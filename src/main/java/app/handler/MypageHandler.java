package app.handler;

import exception.ErrorException;
import http.HttpMethod;
import http.HttpStatus;
import web.filter.authentication.AuthenticationInfo;
import web.handler.SingleArgHandler;
import web.response.DynamicViewResponse;
import web.response.HandlerResponse;

import java.io.File;

import static config.DatabaseConfig.BASIC_PROFILE_IMG;
import static config.DatabaseConfig.PROFILE_IMG_DIR;

public class MypageHandler extends SingleArgHandler<AuthenticationInfo> {
    public MypageHandler() {
        super(HttpMethod.GET, "/mypage");
    }

    @Override
    public HandlerResponse handle(AuthenticationInfo info) {
        DynamicViewResponse response = DynamicViewResponse.of(HttpStatus.OK, "/mypage/index.html");
        Long userId = info.getUserId().orElseThrow(
                () -> new ErrorException("MypageHandler::User id must exists"));

        File requestedFile = new File(PROFILE_IMG_DIR + "/" + userId.toString() + ".jpeg");
        if (requestedFile.exists() && requestedFile.isFile()){
            response.addModel("profileImageUrl", "/mypage/img/" + userId + ".jpeg");
        } else{
            response.addModel("profileImageUrl", BASIC_PROFILE_IMG);
        }
        response.addModel("defaultProfileImageUrl", BASIC_PROFILE_IMG);

        return response;
    }
}
