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
    private final ExceptionHandlerMapping exceptionHandlerMapping;
    private final WasServlet wasServlet;

    public HttpServlet(WasServlet wasServlet,
                       ExceptionHandlerMapping exceptionHandlerMapping,
                       HttpResponseConverter responseConverter,
                       HttpRequestConverter requestConverter,
                       Socket connection) {
        this.wasServlet = wasServlet;
        this.exceptionHandlerMapping = exceptionHandlerMapping;
        this.responseConverter = responseConverter;
        this.requestConverter = requestConverter;
        this.connection = connection;
    }

    @Override
    public void run() {

        try {

            HttpRequest request = requestConverter.parseRequest(connection);
            HttpResponse response = wasServlet.handle(request);
            responseConverter.sendResponse(response, connection);

        } catch (Exception e){
            /**
             * TODO:
             * ExceptionHandler 또한 HttpResponse를 반환하게 하고
             * finally에 `responseConverter.sendResponse(response, connection);` 를 넣어
             * socket에 write를 하는 포인트를 단일 포인트로 관리
             */
            exceptionHandlerMapping.handle(e, connection);
        } finally {
            try { connection.close(); } catch (Exception ignore) {}
        }
    }
}
