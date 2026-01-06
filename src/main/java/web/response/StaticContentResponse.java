package web.response;

import http.HttpStatus;

public class StaticContentResponse extends WebHandlerResponse {
    private final String path;

    private StaticContentResponse(String path) {
        super(HttpStatus.OK);
        this.path = path;
    }

    public static StaticContentResponse of(String path){
        return new StaticContentResponse(path);
    }

    public String getPath() {
        return path;
    }
}
