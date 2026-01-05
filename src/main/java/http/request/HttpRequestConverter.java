package http.request;

import java.net.Socket;

public interface HttpRequestConverter {
    HttpRequest parseRequest(Socket connection);
}
