package web.dispatch.adapter;

import exception.ErrorException;
import http.request.HttpRequest;
import web.dispatch.argument.ArgumentResolver;
import web.dispatch.HandlerAdapter;
import web.handler.SingleArgHandler;
import web.handler.WebHandler;
import web.response.HandlerResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class SingleArgHandlerAdapter implements HandlerAdapter {
    private final List<ArgumentResolver<?>> resolverList;

    public SingleArgHandlerAdapter(List<ArgumentResolver<?>> resolverList) {
        this.resolverList = resolverList;
    }

    @Override
    public boolean support(WebHandler handler) {
        return handler instanceof SingleArgHandler<?>;
    }

    @Override
    @SuppressWarnings("unchecked")
    public HandlerResponse handle(HttpRequest request, WebHandler handler) {
        SingleArgHandler<?> singleArgHandler = (SingleArgHandler<?>) handler;
        Type argumentType = ((ParameterizedType) singleArgHandler.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Class<?> argumentClass = (Class<?>) argumentType;
        ArgumentResolver<?> resolver = resolverList.stream()
                .filter(r -> r.support(argumentClass))
                .findFirst().orElseThrow(() -> new ErrorException("HandlerAdapterError: No argument resolver supported."));
        return ((SingleArgHandler<Object>) singleArgHandler).handle(
                resolver.resolve(request)
        );
    }

}
