package webserver.exception;

import java.net.Socket;

public interface ExceptionHandler {
    boolean support(Throwable e);
    void handle(Throwable e, Socket connection);
}
