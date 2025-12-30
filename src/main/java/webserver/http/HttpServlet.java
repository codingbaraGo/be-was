package webserver.http;

import webserver.exception.ExceptionHandlerMapping;
import webserver.http.response.HttpResponseConverter;
import webserver.web.WasServlet;
import webserver.http.request.HttpRequestConverter;
import webserver.http.request.HttpRequest;
import webserver.http.response.HttpResponse;

import java.net.Socket;

public class HttpServlet implements Runnable{
    private final Socket connection;
    private final HttpRequestConverter requestConverter;
    private final HttpResponseConverter responseConverter;
    private ExceptionHandlerMapping handlerMapping;
    private WasServlet servlet;

    public HttpServlet(Socket connection, HttpRequestConverter requestConverter, HttpResponseConverter responseConverter){
        this.connection = connection;
        this.requestConverter = requestConverter;
        this.responseConverter = responseConverter;
    }
    @Override
    public void run() {

        try {
            HttpRequest request = requestConverter.parseRequest(connection);
            HttpResponse response = servlet.handle(request);
            responseConverter.sendResponse(response, connection);
        } catch (Exception e){
            handlerMapping.handle(e, connection);
        }
    }
}
