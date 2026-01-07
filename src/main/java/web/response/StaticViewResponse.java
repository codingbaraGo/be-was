package web.response;

import http.HttpStatus;

public class StaticViewResponse extends HandlerResponse {
    private final String path;

    public StaticViewResponse(HttpStatus status, String path){
        super(status);
        this.path = path;
    }

    public static StaticViewResponse of(HttpStatus status, String path){
        return new StaticViewResponse(status, path);
    }

    public static StaticViewResponse of (String path){
        return new StaticViewResponse(HttpStatus.OK, path);
    }

    public String getPath() {
        return path;
    }
}
