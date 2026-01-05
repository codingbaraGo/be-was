package webserver.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.exception.ErrorCode;
import webserver.exception.ErrorException;
import webserver.exception.ServiceException;
import webserver.http.HttpMethod;
import webserver.http.request.HttpRequest;
import webserver.http.response.HttpResponse;
import webserver.web.handler.WebHandler;
import webserver.web.handler.response.WebHandlerResponse;
import webserver.web.handler.response.handler.WebHandlerResponseHandler;

import java.util.*;

public class WasServlet {
    private final Map<HttpMethod, List<WebHandler>> handlerMapping;
    private final List<WebHandlerResponseHandler> responseHandlerList;
    private final Logger logger = LoggerFactory.getLogger(WasServlet.class);

    public WasServlet(List<WebHandler> handlerMapping, List<WebHandlerResponseHandler> responseHandlerList) {
        this.responseHandlerList = responseHandlerList;
        this.handlerMapping = new HashMap<>();
        Arrays.stream(HttpMethod.values()).forEach(m -> this.handlerMapping.put(m, new ArrayList<>()));
        handlerMapping.forEach(hm -> this.handlerMapping.get(hm.getMethod()).add(hm));
    }

    public HttpResponse handle(HttpRequest request){
        HttpMethod method = request.getMethod();
        logger.debug(method.name() + " " + request.getPath() + "  " + request.getQuery() + " from" + request.getRequestAddress());

        WebHandler handler = handlerMapping.get(method).stream()
                .filter(h -> h.checkEndpoint(method, request.getPath()))
                .findFirst().orElseThrow(()-> new ServiceException(ErrorCode.NO_SUCH_RESOURCE));
        WebHandlerResponse response = handler.handle(request);

        WebHandlerResponseHandler responseHandler = responseHandlerList.stream()
                .filter(rh -> rh.supports(response))
                .findFirst().orElseThrow(()-> new ErrorException("Post handler not exists"));
        return responseHandler.handle(response);
    }
}
