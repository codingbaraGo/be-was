package web.response;

import http.HttpStatus;

public abstract class WebHandlerResponse {
    protected final HttpStatus status;

    protected WebHandlerResponse(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus(){
        return status;
    }
}
