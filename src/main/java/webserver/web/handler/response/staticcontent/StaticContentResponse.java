package webserver.web.handler.response.staticcontent;

import webserver.web.handler.response.WebHandlerResponse;

public class StaticContentResponse implements WebHandlerResponse {
    private final String path;

    public String getPath() {
        return path;
    }

    private StaticContentResponse(String path) {
        this.path = path;
    }

    public static StaticContentResponse of(String path){
        return new StaticContentResponse(path);
    }
}
