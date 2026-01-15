package web.dispatch.adapter;

import exception.ErrorException;
import http.request.HttpRequest;
import web.dispatch.HandlerAdapter;
import web.dispatch.argument.ArgumentResolver;
import web.handler.DoubleArgHandler;
import web.handler.SingleArgHandler;
import web.handler.WebHandler;
import web.response.HandlerResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class DoubleArgHandlerAdapter implements HandlerAdapter {
    private final List<ArgumentResolver<?>> resolverList;

    public DoubleArgHandlerAdapter(List<ArgumentResolver<?>> resolverList) {
        this.resolverList = resolverList;
    }

    @Override
    public boolean support(WebHandler handler) {
        return handler instanceof DoubleArgHandler<?,?>;
    }

    @Override
    @SuppressWarnings("unchecked")
    public HandlerResponse handle(HttpRequest request, WebHandler handler) {
        DoubleArgHandler<?,?> doubleArgHandler = (DoubleArgHandler<?,?>) handler;
        Type argumentType1 = ((ParameterizedType) doubleArgHandler.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Class<?> argumentClass1 = (Class<?>) argumentType1;
        Type argumentType2 = ((ParameterizedType) doubleArgHandler.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        Class<?> argumentClass2 = (Class<?>) argumentType2;

        ArgumentResolver<?> resolver1 = resolverList.stream()
                .filter(r -> r.support(argumentClass1))
                .findFirst().orElseThrow(() -> new ErrorException("HandlerAdapterError: No argument resolver1 supported."));
        ArgumentResolver<?> resolver2 = resolverList.stream()
                .filter(r -> r.support(argumentClass2))
                .findFirst().orElseThrow(() -> new ErrorException("HandlerAdapterError: No argument resolver2 supported."));
        return ((DoubleArgHandler<Object,Object>) doubleArgHandler).handle(
                resolver1.resolve(request),
                resolver2.resolve(request)
        );
    }

}
