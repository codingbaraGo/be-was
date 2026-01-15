package exception;


import http.HttpStatus;

public enum ErrorCode {
    /* Service Exception */
    MISSING_REGISTER_TOKEN(
            HttpStatus.BAD_REQUEST, "400_MISSING_REGISTER_TOKEN", "회원가입에 필요한 토큰이 누락되었습니다."),
    LOGIN_FAILED(
            HttpStatus.BAD_REQUEST, "400_LOGIN_FAILED", "아이디 혹은 비밀번호가 잘못되었습니다."),
    EMAIL_LENGTH_INVALID(HttpStatus.BAD_REQUEST, "400_EMAIL_LENGTH_INVALID", "이메일은 4 ~ 50글자 사이여야합니다."),
    NICKNAME_LENGTH_INVALID(HttpStatus.BAD_REQUEST, "400_NICKNAME_LENGTH_INVALID", "닉네임은 4 ~ 12글자 사이여야합니다."),
    PASSWORD_LENGTH_INVALID(HttpStatus.BAD_REQUEST, "400_PASSWORD_LENGTH_INVALID", "비밀번호는 4 ~ 16글자 사이여야합니다."),

    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "404_EMAIL_NOT_FOUND", "존재하지 않는 이메일입니다."),

    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "409_EMAIL_ALREADY_EXISTS", "이미 가입된 Email입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "409_NICKNAME_ALREADY_EXISTS", "이미 사용중인 닉네임입니다."),

    /* Internal Error */
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500_INTERNAL", "서버 내부 오류가 발생했습니다."),

    /* Request Error */
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "400_INVALID_INPUT", "입력 값이 올바르지 않습니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "400_MISSING_PARAM", "필수 파라미터가 누락되었습니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "400_VALIDATION_FAIL", "유효성 검증에 실패했습니다."),

    FORBIDDEN(HttpStatus.FORBIDDEN, "403_FORBIDDEN", "권한이 없습니다."),

    NO_SUCH_RESOURCE(HttpStatus.NOT_FOUND, "404_NO_SUCH_RESOURCE", "요청한 리소스를 찾을 수 없습니다."),

    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "405_METHOD_NOT_ALLOWED", "허용되지 않은 HTTP 메서드입니다."),
    ;


    private final HttpStatus status;
    private final String code;
    private final String message;

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
