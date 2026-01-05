package config;

import app.handler.RegisterHandlerImpl;
import exception.ExceptionHandlerMapping;
import exception.handler.ErrorExceptionHandler;
import exception.handler.ServiceExceptionHandler;
import exception.handler.UnhandledErrorHandler;
import http.request.HttpBufferedReaderRequestConverter;
import http.request.HttpRequestConverter;
import http.response.HttpBufferedStreamResponseConverter;
import http.response.HttpResponseConverter;
import web.dispatch.Dispatcher;
import web.handler.StaticContentHandler;
import web.handler.WebHandler;
import web.posthandler.StaticContentResponseHandler;
import web.posthandler.ViewResponseHandler;
import web.posthandler.WebHandlerResponseHandler;

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
    public Dispatcher dispatcher(){
        return new Dispatcher(
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
