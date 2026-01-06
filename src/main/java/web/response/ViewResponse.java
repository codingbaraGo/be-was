package web.response;

import http.HttpStatus;

public class ViewResponse extends WebHandlerResponse {
    private String viewPath;

    public ViewResponse(String viewPath) {
        super(HttpStatus.OK);
        this.viewPath = viewPath;
    }

    public ViewResponse(HttpStatus status){
        super(status);
        this.viewPath = "";
    }

    public static ViewResponse of(HttpStatus status){
        return new ViewResponse(status);
    }
    public static ViewResponse of(HttpStatus status, String viewPath){
        ViewResponse response = new ViewResponse(status);
        response.setViewPath(viewPath);
        return response;
    }

    public static ViewResponse of (String path){
        return new ViewResponse(path);
    }

    public String getViewPath() {
        return viewPath;
    }

    public void setViewPath(String viewPath){
        this.viewPath = viewPath;
    }
}
