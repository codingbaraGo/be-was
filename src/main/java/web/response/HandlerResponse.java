package web.response;

import http.HttpStatus;
import http.response.ResponseCookie;

import java.util.ArrayList;
import java.util.List;

public abstract class HandlerResponse {
    protected final HttpStatus status;
    protected final List<ResponseCookie> cookies;


    protected HandlerResponse(HttpStatus status) {
        this.status = status;
        this.cookies = new ArrayList<>();
    }

    public HttpStatus getStatus(){
        return status;
    }

    public void setCookie(ResponseCookie cookie){
        this.cookies.add(cookie);
    }

    public List<String> getCookies(){
        return this.cookies.stream().map(ResponseCookie::toHeaderValue).toList();
    }
}
