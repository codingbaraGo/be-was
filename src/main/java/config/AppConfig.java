package config;

import app.db.ArticleRepository;
import app.db.UserRepository;
import app.handler.*;
import database.ConnectionManager;
import database.H2DbManager;
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
import web.dispatch.adapter.DoubleArgHandlerAdapter;
import web.dispatch.adapter.SingleArgHandlerAdapter;
import web.dispatch.argument.ArgumentResolver;
import web.dispatch.argument.resolver.*;
import web.filter.*;
import web.handler.DefaultViewHandler;
import web.handler.StaticContentHandler;
import web.handler.WebHandler;
import web.renderer.DynamicViewRenderer;
import web.renderer.HttpResponseRenderer;
import web.renderer.RedirectRenderer;
import web.renderer.StaticViewRenderer;
import web.renderer.view.ExpressionResolver;
import web.session.SessionStorage;
import web.renderer.view.TemplateEngine;
import web.renderer.view.TemplateLoader;

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
                        httpResponseRendererList()
                )
        );
    }

    public List<WebHandler> webHandlerList() {
        return getOrCreate(
                "webHandlerList",
                () -> List.of(
                        staticContentHandler(),
                        registerWithGet(),
                        registerWithPost(),
                        loginWithPost(),
                        logoutWithPost(),
                        homeHandler(),
                        defaultViewHandler())
        );
    }

    // ===== Handler =====
    public StaticContentHandler staticContentHandler() {
        return getOrCreate(
                "staticContentHandler",
                StaticContentHandler::new
        );
    }

    public DefaultViewHandler defaultViewHandler(){
        return getOrCreate("defaultViewHandler", DefaultViewHandler::new);
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
                () -> new RegisterWithPost(userRepository()));
    }

    public LoginWithPost loginWithPost() {
        return getOrCreate("loginWithPost",
                () -> new LoginWithPost(
                        sessionStorage(),
                        userRepository()));
    }

    public LogoutWithPost logoutWithPost(){
        return getOrCreate("logoutWithPost",
                () -> new LogoutWithPost(sessionStorage()));
    }

    public HomeHandler homeHandler(){
        return getOrCreate("homeHandler", HomeHandler::new);
    }

    // ===== Renderer =====
    public List<HttpResponseRenderer> httpResponseRendererList() {
        return getOrCreate(
                "httpResponseRendererList",
                () -> List.of(
                        staticViewRenderer(),
                        redirectRenderer(),
                        dynamicViewRenderer()
                )
        );
    }

    public StaticViewRenderer staticViewRenderer() {
        return getOrCreate(
                "staticViewRenderer",
                StaticViewRenderer::new
        );
    }

    public RedirectRenderer redirectRenderer() {
        return getOrCreate("redirectRenderer", RedirectRenderer::new);
    }

    public DynamicViewRenderer dynamicViewRenderer() {
        return getOrCreate("dynamicViewRenderer",
                () -> new DynamicViewRenderer(templateEngine()));
    }

    // ===== ViewEngine =====
    public TemplateEngine templateEngine() {
        return getOrCreate("templateEngine",
                () -> new TemplateEngine(templateLoader(), expressionResolver()));
    }

    public ExpressionResolver expressionResolver(){
        return getOrCreate("expressResolver", ExpressionResolver::new);
    }

    public TemplateLoader templateLoader() {
        return getOrCreate("templateLoader", TemplateLoader::new);
    }

    // ===== Adapter =====
    public List<HandlerAdapter> handlerAdapterList() {
        return getOrCreate(
                "handlerAdapterList",
                () -> List.of(
                        singleArgHandlerAdapter(),
                        doubleArgHandlerAdapter(),
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

    public DoubleArgHandlerAdapter doubleArgHandlerAdapter(){
        return getOrCreate(
                DoubleArgHandlerAdapter.class.getSimpleName(),
                () -> new DoubleArgHandlerAdapter(
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
                        queryParamsResolver(),
                        multipartFormResolver(),
                        authenticationInfoResolver()
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

    public MultipartFormResolver multipartFormResolver(){
        return getOrCreate("multipartFormResolver",
                () -> new MultipartFormResolver(multipartFormParser()));
    }

    public MultipartFormParser multipartFormParser(){
        return getOrCreate("multipartFormParser", MultipartFormParser::new);
    }

    public AuthenticationInfoResolver authenticationInfoResolver(){
        return getOrCreate(
                AuthenticationInfoResolver.class.getSimpleName(),
                AuthenticationInfoResolver::new);
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
                                unhandledErrorHandler())));
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

    /**
     * ===== Filter =====
     */
    public FilterChainContainer filterChainContainer(){
        return getOrCreate("filterChainContainer",
                () -> new FilterChainContainer(dispatcher()));
    }

    public AccessLogFilter accessLogFilter(){
        return getOrCreate("accessLogFilter", AccessLogFilter::new);
    }

    public RestrictedFilter restrictedFilter(){
        return getOrCreate("restrictedFilter", RestrictedFilter::new);
    }

    public AuthenticationFilter authenticationFilter() {
        return getOrCreate("authenticationFilter",
                () -> new AuthenticationFilter(sessionStorage()));
    }

    public MemberAuthorizationFilter memberAuthorizationFilter(){
        return getOrCreate("memberAuthorizationFilter",
                MemberAuthorizationFilter::new);
    }

    public UnanimousAuthorizationFilter unanimousAuthorizationFilter(){
        return getOrCreate("unanimousAuthorizationFilter",
                UnanimousAuthorizationFilter::new);
    }

    public SessionStorage sessionStorage() {
        return getOrCreate("sessionStorage",
                SessionStorage::new);
    }

    /**
     * ===== DB =====
     */
    public ConnectionManager connectionManager(){
        return h2DbManager();
    }

    public H2DbManager h2DbManager(){
        return getOrCreate(H2DbManager.class.getSimpleName(), H2DbManager::new);
    }

    public DdlGenerator ddlGenerator(){
        return getOrCreate(DdlGenerator.class.getSimpleName(),
                () -> new DdlGenerator(connectionManager()));
    }

    public UserRepository userRepository(){
        return getOrCreate(UserRepository.class.getSimpleName(),
                ()-> new UserRepository(connectionManager()));
    }

    public ArticleRepository articleRepository(){
        return getOrCreate(ArticleRepository.class.getSimpleName(),
                () -> new ArticleRepository(connectionManager()));
    }
}

