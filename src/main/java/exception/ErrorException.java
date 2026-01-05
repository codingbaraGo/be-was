package exception;

public class ErrorException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Throwable throwable;

    public ErrorException(String message) {
        super(message);
        this.errorCode = ErrorCode.INTERNAL_ERROR;
        this.throwable = null;
    }

    public ErrorException(String message, Throwable t) {
        super(message);
        this.errorCode = ErrorCode.INTERNAL_ERROR;
        this.throwable = t;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
