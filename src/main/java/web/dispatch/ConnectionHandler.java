package web.dispatch;

import exception.ExceptionHandlerMapping;
import http.response.HttpResponseConverter;
import http.request.HttpRequestConverter;
import http.request.HttpRequest;
import http.response.HttpResponse;
import web.filter.FilterChainContainer;

import java.net.Socket;

public class ConnectionHandler implements Runnable{
    private final Socket connection;
    private final FilterChainContainer filterChainContainer;
    private final HttpRequestConverter requestConverter;
    private final HttpResponseConverter responseConverter;
    private final ExceptionHandlerMapping exceptionHandlerMapping;

    public ConnectionHandler(FilterChainContainer filterChainContainer,
                             ExceptionHandlerMapping exceptionHandlerMapping,
                             HttpResponseConverter responseConverter,
                             HttpRequestConverter requestConverter,
                             Socket connection) {
        this.filterChainContainer = filterChainContainer;
        this.exceptionHandlerMapping = exceptionHandlerMapping;
        this.responseConverter = responseConverter;
        this.requestConverter = requestConverter;
        this.connection = connection;
    }

    @Override
    public void run() {

        HttpResponse response = HttpResponse.of();
        try {

            HttpRequest request = requestConverter.parseRequest(connection);
            filterChainContainer.runFilterChain(request,response);
            responseConverter.sendResponse(response, connection);

        } catch (Throwable t){
            /**
             * TODO:
             * ExceptionHandler 또한 HttpResponse를 반환하게 하고
             * finally에 `responseConverter.sendResponse(response, connection);` 를 넣어
             * socket에 write를 하는 포인트를 단일 포인트로 관리
             */
            exceptionHandlerMapping.handle(t, connection);
        } finally {
            try { connection.close(); } catch (Throwable ignore) {}
        }
    }
}
