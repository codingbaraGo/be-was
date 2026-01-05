package web.web.handler.response.staticcontent;

import web.web.handler.response.WebHandlerResponse;

public class StaticContentResponse implements WebHandlerResponse {
    private final String path;

    private StaticContentResponse(String path) {
        this.path = path;
    }

    public static StaticContentResponse of(String path){
        return new StaticContentResponse(path);
    }

    public String getPath() {
        return path;
    }
}
