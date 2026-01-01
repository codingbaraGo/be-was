package webserver.http.response;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpBufferedStreamResponseConverter implements HttpResponseConverter {

    @Override
    public boolean support() {
        return true;
    }

    @Override
    public void sendResponse(HttpResponse response, Socket connection) {
        try {
            OutputStream raw = connection.getOutputStream();
            BufferedOutputStream out = new BufferedOutputStream(raw);

            byte[] body = response.getBody();
            if (body == null) body = new byte[0];

            String statusLine =
                    "HTTP/1.1 " + response.getStatus().getCode() + " " + response.getStatus() + "\r\n";
            out.write(statusLine.getBytes(StandardCharsets.ISO_8859_1));

            for (String key : response.getHeaders()) {
                List<String> values = response.getHeader(key);
                for (String value : values) {
                    String headerLine = key + ": " + value + "\r\n";
                    out.write(headerLine.getBytes(StandardCharsets.ISO_8859_1));
                }
            }

            out.write("\r\n".getBytes(StandardCharsets.ISO_8859_1));
            if (body.length > 0) {
                out.write(body);
            }
            out.flush();

        } catch (IOException e) {
            throw new IllegalStateException("Failed to send HTTP response", e);
        }
    }
}
