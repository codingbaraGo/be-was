package webserver.web.handler.response.view;

import webserver.web.handler.response.WebHandlerResponse;

public class ViewResponse implements WebHandlerResponse {
    String viewPath;

    public String getViewPath() {
        return viewPath;
    }

    public ViewResponse(String viewPath) {
        this.viewPath = viewPath;
    }

    public static ViewResponse of (String path){
        return new ViewResponse(path);
    }
}
