package web.web.handler.response.view;

import web.web.handler.response.WebHandlerResponse;

public class ViewResponse implements WebHandlerResponse {
    private final String viewPath;

    public ViewResponse(String viewPath) {
        this.viewPath = viewPath;
    }

    public static ViewResponse of (String path){
        return new ViewResponse(path);
    }

    public String getViewPath() {
        return viewPath;
    }
}
