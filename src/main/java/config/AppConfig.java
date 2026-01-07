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
import web.dispatch.ArgumentResolver;
import web.dispatch.Dispatcher;
import web.dispatch.HandlerAdapter;
import web.dispatch.adapter.DefaultHandlerAdapter;
import web.dispatch.adapter.SingleArgHandlerAdapter;
import web.dispatch.argument.HttpRequestResolver;
import web.dispatch.argument.QueryParamsResolver;
import web.handler.StaticContentHandler;
import web.handler.WebHandler;
import web.renderer.StaticViewRenderer;
import web.renderer.HttpResponseRenderer;

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
                handlerAdapterList(),
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

    private List<HttpResponseRenderer> webHandlerResponseHandlerList(){
        return List.of(
                staticViewResponseHandler()
        );
    }
    private StaticContentHandler staticContentHandler(){
        return new StaticContentHandler();
    }
    private StaticViewRenderer staticViewResponseHandler(){
        return new StaticViewRenderer();
    }


    //Adapter
    public List<HandlerAdapter> handlerAdapterList(){
        return List.of(
                singleArgHandlerAdapter(),
                defaultHandlerAdapter()
        );
    }

    private SingleArgHandlerAdapter singleArgHandlerAdapter(){
        return new SingleArgHandlerAdapter(
                argumentResolverList()
        );
    }
    private DefaultHandlerAdapter defaultHandlerAdapter(){
        return new DefaultHandlerAdapter();
    }

    //Resolver
    public List<ArgumentResolver<?>> argumentResolverList(){
        return List.of(
                httpRequestResolver(),
                queryParamsResolver()
        );
    }

    private HttpRequestResolver httpRequestResolver(){
        return new HttpRequestResolver();
    }
    private QueryParamsResolver queryParamsResolver(){
        return new QueryParamsResolver();
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
