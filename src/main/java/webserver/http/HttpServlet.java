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
    private final ExceptionHandlerMapping handlerMapping;
    private final WasServlet servlet;

    public HttpServlet(WasServlet servlet,
                       ExceptionHandlerMapping handlerMapping,
                       HttpResponseConverter responseConverter,
                       HttpRequestConverter requestConverter,
                       Socket connection) {
        this.servlet = servlet;
        this.handlerMapping = handlerMapping;
        this.responseConverter = responseConverter;
        this.requestConverter = requestConverter;
        this.connection = connection;
    }

    @Override
    public void run() {

        try {
            HttpRequest request = requestConverter.parseRequest(connection);
            HttpResponse response = servlet.handle(request);
            responseConverter.sendResponse(response, connection);
        } catch (Exception e){
            handlerMapping.handle(e, connection);
        } finally {
            try { connection.close(); } catch (Exception ignore) {}
        }
    }
}
