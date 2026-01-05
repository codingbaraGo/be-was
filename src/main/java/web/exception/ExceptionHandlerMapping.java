package web.exception;

import java.net.Socket;
import java.util.List;

public class ExceptionHandlerMapping {
    private final   List<ExceptionHandler> handlers;

    public ExceptionHandlerMapping (List<ExceptionHandler> handlers){
        this.handlers = handlers;
    }

    public void handle(Throwable e, Socket connection){
        ExceptionHandler handler = handlers.stream().filter(h -> h.support(e)).findFirst().orElseThrow();
        handler.handle(e, connection);
    }

}
