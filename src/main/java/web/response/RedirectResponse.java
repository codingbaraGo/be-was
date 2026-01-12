package web.response;

import http.HttpStatus;

public class RedirectResponse extends HandlerResponse {
    private final String location;

    private RedirectResponse(String location) {
        super(HttpStatus.FOUND);
        this.location = location;
    }

    public static RedirectResponse to(String location) {
        return new RedirectResponse(location);
    }

    public String getLocation() { return location; }
}
