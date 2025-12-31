package webserver.exception;

import java.net.Socket;
import java.util.List;

public class ExceptionHandlerMapping {
    private final   List<ExceptionHandler> handlers;

    public ExceptionHandlerMapping (List<ExceptionHandler> handlers){
        this.handlers = handlers;
    }

    public void handle(Exception e, Socket connection){
        ExceptionHandler handlerAdaptor = handlers.stream().filter(handler -> handler.support(e)).findFirst().orElseThrow();
        handlerAdaptor.handle(e, connection);
    }

}
