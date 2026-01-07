package web.dispatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import exception.ErrorCode;
import exception.ErrorException;
import exception.ServiceException;
import http.HttpMethod;
import http.request.HttpRequest;
import http.response.HttpResponse;
import web.handler.WebHandler;
import web.response.HandlerResponse;
import web.renderer.HttpResponseRenderer;

import java.util.*;

public class Dispatcher {
    private final Map<HttpMethod, List<WebHandler>> handlerMapping;
    private final List<HandlerAdapter> adapterList;
    private final List<HttpResponseRenderer> responseHandlerList;
    private final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    public Dispatcher(List<WebHandler> handlerMapping, List<HandlerAdapter> adapterList, List<HttpResponseRenderer> responseHandlerList) {
        this.adapterList = adapterList;
        this.responseHandlerList = responseHandlerList;
        this.handlerMapping = new HashMap<>();
        Arrays.stream(HttpMethod.values()).forEach(m -> this.handlerMapping.put(m, new ArrayList<>()));
        handlerMapping.forEach(hm -> this.handlerMapping.get(hm.getMethod()).add(hm));
    }

    public HttpResponse handle(HttpRequest request){
        logger.debug("{}: {} - {} from {}",
                request.getMethod(), request.getPath(), request.getQueryString(), request.getRequestAddress());

        WebHandler handler = handlerMapping.get(request.getMethod()).stream()
                .filter(h -> h.checkEndpoint(request.getMethod(), request.getPath()))
                .findFirst().orElseThrow(()-> new ServiceException(ErrorCode.NO_SUCH_RESOURCE));

        HandlerAdapter adapter = adapterList.stream().filter(ha -> ha.support(handler))
                .findFirst().orElseThrow(() -> new ErrorException("DispatcherError: No adapter matched"));

        HandlerResponse response = adapter.handle(request, handler);

        HttpResponseRenderer responseHandler = responseHandlerList.stream()
                .filter(rh -> rh.supports(response))
                .findFirst().orElseThrow(()-> new ErrorException("Post handler not exists"));
        return responseHandler.handle(response);
    }
}
