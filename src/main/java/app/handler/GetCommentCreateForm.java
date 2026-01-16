package app.handler;

import http.HttpMethod;
import http.HttpStatus;
import web.dispatch.argument.QueryParameters;
import web.handler.SingleArgHandler;
import web.response.DynamicViewResponse;
import web.response.HandlerResponse;

public class GetCommentCreateForm extends SingleArgHandler<QueryParameters> {
    public GetCommentCreateForm() {
        super(HttpMethod.GET, "/comment");
    }

    @Override
    public HandlerResponse handle(QueryParameters arg) {
        DynamicViewResponse response = DynamicViewResponse.of(HttpStatus.OK, "/comment/index.html");
        response.addModel("articleId", arg.getValidQueryValue("articleId"));
        return response;
    }
}
