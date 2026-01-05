package exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import exception.ExceptionHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class UnhandledErrorHandler implements ExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(UnhandledErrorHandler.class);
    @Override
    public boolean support(Throwable e) {
        return e instanceof Throwable;
    }

    @Override
    public void handle(Throwable t, Socket connection) {
        String body = "{\"code\":\"500_UNHANDLED\",\"message\":\"서버 내부 오류가 발생했습니다.\"}";
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        logger.debug(t.getMessage());
        String header =
                "HTTP/1.1 500 Internal Server Error\r\n" +
                        "Date: " + DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()) + "\r\n" +
                        "Server: be-was\r\n" +
                        "Connection: close\r\n" +
                        "Content-Type: application/json; charset=utf-8\r\n" +
                        "Content-Length: " + bodyBytes.length + "\r\n" +
                        "\r\n";

        try (OutputStream out = connection.getOutputStream()) {
            out.write(header.getBytes(StandardCharsets.US_ASCII));
            out.write(bodyBytes);
            out.flush();
        } catch (IOException ignored) {
        }
    }
}
