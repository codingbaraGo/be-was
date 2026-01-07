package web.response;

import http.HttpStatus;

public abstract class HandlerResponse {
    protected final HttpStatus status;

    protected HandlerResponse(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus(){
        return status;
    }
}
