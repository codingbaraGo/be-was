package web.dispatch.argument.resolver;

import exception.ErrorCode;
import exception.ServiceException;
import http.request.HttpRequest;
import web.dispatch.argument.ArgumentResolver;
import web.dispatch.argument.MultipartForm;

public class MultipartFormResolver extends ArgumentResolver<MultipartForm> {
    private final MultipartFormParser parser;

    public MultipartFormResolver(MultipartFormParser parser) {
        this.parser = parser;
    }

    @Override
    public MultipartForm resolve(HttpRequest request) {
        String contentType = request.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/form-data")) {
            throw new ServiceException(ErrorCode.INVALID_INPUT, "multipart/form-data required");
        }
        return parser.parse(request.getBody(), contentType);
    }
}
