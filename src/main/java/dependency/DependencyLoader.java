package dependency;

import webserver.exception.ExceptionHandlerMapping;
import webserver.http.request.HttpRequestConverter;
import webserver.http.response.HttpResponseConverter;
import webserver.web.WasServlet;

public class DependencyLoader {
    private final AppConfig appConfig;

    public final HttpRequestConverter httpRequestConverter;
    public final HttpResponseConverter httpResponseConverter;
    public final ExceptionHandlerMapping exceptionHandlerMapping;
    public final WasServlet wasServlet;

    public DependencyLoader(){
        this.appConfig = new AppConfig();
        this.httpRequestConverter = appConfig.httpRequestConverter();
        this.httpResponseConverter = appConfig.httpResponseConverter();
        this.exceptionHandlerMapping = appConfig.exceptionHandlerMapping();
        this.wasServlet = appConfig.wasServlet();
    }
}
