package dependency;

import app.handler.RegisterHandlerImpl;
import webserver.exception.ExceptionHandlerMapping;
import webserver.exception.handler.ErrorExceptionHandler;
import webserver.exception.handler.ServiceExceptionHandler;
import webserver.exception.handler.UnhandledErrorHandler;
import webserver.http.request.HttpBufferedReaderRequestConverter;
import webserver.http.request.HttpRequestConverter;
import webserver.http.response.HttpBufferedStreamResponseConverter;
import webserver.http.response.HttpResponseConverter;
import webserver.web.WasServlet;
import webserver.web.handler.StaticContentHandler;
import webserver.web.handler.WebHandler;
import webserver.web.handler.response.handler.StaticContentResponseHandler;
import webserver.web.handler.response.handler.ViewResponseHandler;
import webserver.web.handler.response.handler.WebHandlerResponseHandler;

import java.util.List;

public class AppConfig {
    //Http
    public HttpBufferedReaderRequestConverter httpBufferedReaderRequestConverter(){
        return new HttpBufferedReaderRequestConverter();
    }

    public HttpBufferedStreamResponseConverter httpBufferedStreamResponseConverter(){
        return new HttpBufferedStreamResponseConverter();
    }

    public HttpRequestConverter httpRequestConverter(){
        return httpBufferedReaderRequestConverter();
    }
    public HttpResponseConverter httpResponseConverter(){
        return httpBufferedStreamResponseConverter();
    }


    //Web
    public WasServlet wasServlet(){
        return new WasServlet(
                webHandlerList(),
                webHandlerResponseHandlerList()
        );
    }

    private List<WebHandler> webHandlerList(){
        return List.of(
                staticContentHandler(),
                registerHandlerImpl()
        );
    }
    private RegisterHandlerImpl registerHandlerImpl(){
        return new RegisterHandlerImpl();
    }

    private List<WebHandlerResponseHandler> webHandlerResponseHandlerList(){
        return List.of(
                staticContentResponseHandler(),
                viewResponseHandler()
        );
    }
    private StaticContentHandler staticContentHandler(){
        return new StaticContentHandler();
    }
    private ViewResponseHandler viewResponseHandler(){
        return new ViewResponseHandler();
    }
    private StaticContentResponseHandler staticContentResponseHandler(){
        return new StaticContentResponseHandler();
    }


    //Exception
    public ExceptionHandlerMapping exceptionHandlerMapping(){
        return new ExceptionHandlerMapping(
                List.of(
                        serviceExceptionHandler(),
                        errorExceptionHandler(),
                        unhandledErrorHandler()
                )
        );
    }

    private ServiceExceptionHandler serviceExceptionHandler(){
        return new ServiceExceptionHandler();
    }
    private UnhandledErrorHandler unhandledErrorHandler(){
        return new UnhandledErrorHandler();
    }
    private ErrorExceptionHandler errorExceptionHandler(){
        return new ErrorExceptionHandler();
    }
}
