package web.dispatch.argument;

import http.request.HttpRequest;

import java.lang.reflect.ParameterizedType;

public abstract class ArgumentResolver<T> {
    public boolean support(Class<?> type){
        return type.equals(resolveType());
    }
    public abstract T resolve(HttpRequest request);

    @SuppressWarnings("unchecked")
    protected Class<T> resolveType() {
        ParameterizedType pt =
                (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) pt.getActualTypeArguments()[0];
    }
}
