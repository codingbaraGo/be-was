package web.dispatch.argument.resolver;

import exception.ErrorException;
import http.request.HttpRequest;
import web.dispatch.argument.ArgumentResolver;
import web.dispatch.argument.QueryParameters;
import java.nio.charset.StandardCharsets;

public class QueryParamsResolver extends ArgumentResolver<QueryParameters> {

    @Override
    public QueryParameters resolve(HttpRequest request) {
            String queryString = request.getQueryString();
            if(queryString==null) {
                if (request.getContentType().strip().equalsIgnoreCase("application/x-www-form-urlencoded")) {
                    queryString = new String(request.getBody(), StandardCharsets.UTF_8);
                } else throw new ErrorException("QueryParameterError: no query params");    //TODO: 이 부분을 ErrorException으로 처리해야할지, ServiceException으로 처리해야 할지 고민
            }
        return QueryParameters.of(queryString);
    }
}
