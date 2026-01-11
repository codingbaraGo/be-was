package config;

import app.handler.RegisterWithGet;
import app.handler.RegisterWithPost;
import exception.ExceptionHandlerMapping;
import exception.handler.ErrorExceptionHandler;
import exception.handler.ServiceExceptionHandler;
import exception.handler.UnhandledErrorHandler;
import http.request.BufferedReaderHttpRequestConverter;
import http.request.HttpRequestConverter;
import http.request.InputStreamHttpRequestConverter;
import http.response.HttpResponseBufferedStreamConverter;
import http.response.HttpResponseConverter;
import web.dispatch.Dispatcher;
import web.dispatch.HandlerAdapter;
import web.dispatch.adapter.DefaultHandlerAdapter;
import web.dispatch.adapter.SingleArgHandlerAdapter;
import web.dispatch.argument.ArgumentResolver;
import web.dispatch.argument.resolver.HttpRequestResolver;
import web.dispatch.argument.resolver.QueryParamsResolver;
import web.handler.StaticContentHandler;
import web.handler.WebHandler;
import web.renderer.HttpResponseRenderer;
import web.renderer.StaticViewRenderer;

import java.util.List;

public class AppConfig extends SingletonContainer {

    /**
     * ===== Http =====
     */
    public HttpRequestConverter httpRequestConverter() {
        return inputStreamHttpRequestConverter();
    }

    public HttpResponseConverter httpResponseConverter() {
        return httpResponseBufferedStreamConverter();
    }

    public BufferedReaderHttpRequestConverter httpBufferedReaderRequestConverter() {
        return getOrCreate(
                "httpBufferedReaderRequestConverter",
                BufferedReaderHttpRequestConverter::new
        );
    }

    public HttpResponseBufferedStreamConverter httpResponseBufferedStreamConverter() {
        return getOrCreate(
                "httpResponseBufferedStreamConverter",
                HttpResponseBufferedStreamConverter::new
        );
    }

    public InputStreamHttpRequestConverter inputStreamHttpRequestConverter() {
        return getOrCreate(
                "inputStreamHttpRequestConverter",
                InputStreamHttpRequestConverter::new
        );
    }

    /**
     * ===== Web =====
     */
    public Dispatcher dispatcher() {
        return getOrCreate(
                "dispatcher",
                () -> new Dispatcher(
                        webHandlerList(),
                        handlerAdapterList(),
                        webHandlerResponseHandlerList()
                )
        );
    }

    public List<WebHandler> webHandlerList() {
        return getOrCreate(
                "webHandlerList",
                () -> List.of(
                        staticContentHandler(),
                        registerWithGet(),
                        registerWithPost()
                )
        );
    }

    public RegisterWithGet registerWithGet() {
        return getOrCreate(
                "registerWithGet",
                RegisterWithGet::new
        );
    }

    public RegisterWithPost registerWithPost() {
        return getOrCreate(
                "registerWithPost",
                RegisterWithPost::new
        );
    }

    public List<HttpResponseRenderer> webHandlerResponseHandlerList() {
        return getOrCreate(
                "webHandlerResponseHandlerList",
                () -> List.of(
                        staticViewResponseHandler()
                )
        );
    }

    public StaticContentHandler staticContentHandler() {
        return getOrCreate(
                "staticContentHandler",
                StaticContentHandler::new
        );
    }

    public StaticViewRenderer staticViewResponseHandler() {
        return getOrCreate(
                "staticViewResponseHandler",
                StaticViewRenderer::new
        );
    }

    // ===== Adapter =====
    public List<HandlerAdapter> handlerAdapterList() {
        return getOrCreate(
                "handlerAdapterList",
                () -> List.of(
                        singleArgHandlerAdapter(),
                        defaultHandlerAdapter()
                )
        );
    }

    public SingleArgHandlerAdapter singleArgHandlerAdapter() {
        return getOrCreate(
                "singleArgHandlerAdapter",
                () -> new SingleArgHandlerAdapter(
                        argumentResolverList()
                )
        );
    }

    public DefaultHandlerAdapter defaultHandlerAdapter() {
        return getOrCreate(
                "defaultHandlerAdapter",
                DefaultHandlerAdapter::new
        );
    }

    // ===== Resolver =====
    public List<ArgumentResolver<?>> argumentResolverList() {
        return getOrCreate(
                "argumentResolverList",
                () -> List.of(
                        httpRequestResolver(),
                        queryParamsResolver()
                )
        );
    }

    public HttpRequestResolver httpRequestResolver() {
        return getOrCreate(
                "httpRequestResolver",
                HttpRequestResolver::new
        );
    }

    public QueryParamsResolver queryParamsResolver() {
        return getOrCreate(
                "queryParamsResolver",
                QueryParamsResolver::new
        );
    }

    /**
     * ===== Exception =====
     */
    public ExceptionHandlerMapping exceptionHandlerMapping() {
        return getOrCreate(
                "exceptionHandlerMapping",
                () -> new ExceptionHandlerMapping(
                        List.of(
                                serviceExceptionHandler(),
                                errorExceptionHandler(),
                                unhandledErrorHandler()
                        )
                )
        );
    }

    public ServiceExceptionHandler serviceExceptionHandler() {
        return getOrCreate(
                "serviceExceptionHandler",
                ServiceExceptionHandler::new
        );
    }

    public UnhandledErrorHandler unhandledErrorHandler() {
        return getOrCreate(
                "unhandledErrorHandler",
                UnhandledErrorHandler::new
        );
    }

    public ErrorExceptionHandler errorExceptionHandler() {
        return getOrCreate(
                "errorExceptionHandler",
                ErrorExceptionHandler::new
        );
    }
}
