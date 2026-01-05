package config;

import web.exception.ExceptionHandlerMapping;
import http.request.HttpRequestConverter;
import http.response.HttpResponseConverter;
import web.dispatch.Dispatcher;

public class DependencyLoader {
    private final AppConfig appConfig;

    public final HttpRequestConverter httpRequestConverter;
    public final HttpResponseConverter httpResponseConverter;
    public final ExceptionHandlerMapping exceptionHandlerMapping;
    public final Dispatcher dispatcher;

    public DependencyLoader(){
        this.appConfig = new AppConfig();
        this.httpRequestConverter = appConfig.httpRequestConverter();
        this.httpResponseConverter = appConfig.httpResponseConverter();
        this.exceptionHandlerMapping = appConfig.exceptionHandlerMapping();
        this.dispatcher = appConfig.wasServlet();
    }
}
