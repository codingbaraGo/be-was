package web.response;

import http.HttpStatus;

public class StaticViewResponse extends HandlerResponse {
    private String path;

    public StaticViewResponse(String path) {
        super(HttpStatus.OK);
        this.path = path;
    }

    public StaticViewResponse(HttpStatus status){
        super(status);
        this.path = "";
    }

    public static StaticViewResponse of(HttpStatus status){
        return new StaticViewResponse(status);
    }
    public static StaticViewResponse of(HttpStatus status, String viewPath){
        StaticViewResponse response = new StaticViewResponse(status);
        response.setPath(viewPath);
        return response;
    }

    public static StaticViewResponse of (String path){
        return new StaticViewResponse(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path){
        this.path = path;
    }
}
