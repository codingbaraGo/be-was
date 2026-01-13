package app.handler;

import http.HttpMethod;
import http.request.HttpRequest;
import http.response.CookieBuilder;
import web.handler.SingleArgHandler;
import web.response.HandlerResponse;
import web.response.RedirectResponse;
import web.session.SessionStorage;

public class LogoutWithPost extends SingleArgHandler<HttpRequest> {
    private final SessionStorage sessionManager;

    public LogoutWithPost(SessionStorage sessionManager) {
        super(HttpMethod.POST, "/user/logout");
        this.sessionManager = sessionManager;
    }

    @Override
    public HandlerResponse handle(HttpRequest request) {
        String sid = request.getCookieValue("SID").orElse(null);
        if (sid != null) sessionManager.invalidate(sid);

        RedirectResponse response = RedirectResponse.to("/");
        response.setCookie(CookieBuilder.delete("SID").path("/"));
        return response;
    }
}
