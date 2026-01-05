package http.response;

import java.net.Socket;

public interface HttpResponseConverter {
    boolean support();
    void sendResponse(HttpResponse response, Socket connection);
}
