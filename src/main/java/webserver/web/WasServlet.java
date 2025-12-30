package webserver.web;

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

    public WasServlet(List<WebHandler> handlerMapping, List<WebHandlerResponseHandler> responseHandlerList) {
        this.responseHandlerList = responseHandlerList;
        this.handlerMapping = new HashMap<>();
        Arrays.stream(HttpMethod.values()).forEach(m -> this.handlerMapping.put(m, new ArrayList<>()));
        handlerMapping.forEach(hm -> this.handlerMapping.get(hm.getMethod()).add(hm));
    }

    public HttpResponse handle(HttpRequest request){
        HttpMethod method = request.getMethod();
        WebHandler handler = handlerMapping.get(method).stream()
                .filter(h -> h.checkEndpoint(method, request.getPath()))
                .findFirst().orElseThrow(); //TODO: Add Handler not match exception -> no content
        WebHandlerResponse response = handler.handle(request);
        WebHandlerResponseHandler responseHandler = responseHandlerList.stream()
                .filter(rh -> rh.supports(response))
                .findFirst().orElseThrow();//TODO: Add Response handler not match exception -> internal error
        return responseHandler.handle(response);
    }
}
