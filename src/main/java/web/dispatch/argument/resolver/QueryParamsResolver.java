package web.dispatch.argument.resolver;

import http.request.HttpRequest;
import web.dispatch.argument.ArgumentResolver;
import web.dispatch.argument.QueryParameters;
import java.nio.charset.StandardCharsets;

public class QueryParamsResolver extends ArgumentResolver<QueryParameters> {

    @Override
    public QueryParameters resolve(HttpRequest request) {
            String queryString = "";
            queryString += request.getQueryString() == null
                    ? "" : request.getQueryString();
        System.out.println(queryString);

        if (request.getContentType() != null
                    && request.getContentType().strip().equalsIgnoreCase("application/x-www-form-urlencoded")
                    && request.getBody()!=null) {
                queryString += new String(request.getBody(), StandardCharsets.UTF_8);
            }
        System.out.println(queryString);

        return QueryParameters.of(queryString);
    }
}
