package webserver.exception;

import java.net.Socket;

public interface ExceptionHandler {
    boolean support(Exception e);
    void handle(Exception e, Socket connection);
}
