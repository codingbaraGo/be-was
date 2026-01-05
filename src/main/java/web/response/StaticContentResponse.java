package web.response;

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
