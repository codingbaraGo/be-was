package web.dispatch.argument;

import exception.ErrorException;
import http.request.HttpRequest;
import web.dispatch.ArgumentResolver;

public class QueryParamsResolver extends ArgumentResolver<QueryParameters> {

    @Override
    public QueryParameters resolve(HttpRequest request) {
        QueryParameters queryParameters = new QueryParameters();
        for (String key : request.getQueryKeys()) {
            queryParameters.addParams(key, request.getQueryValue(key).orElseThrow(
                    ()-> new ErrorException("query parameter mapping error")));
        }
        return queryParameters;
    }
}
