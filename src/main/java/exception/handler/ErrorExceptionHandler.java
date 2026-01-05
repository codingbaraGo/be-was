package exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import exception.ErrorCode;
import exception.ErrorException;
import exception.ExceptionHandler;
import http.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorExceptionHandler implements ExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(ErrorException.class);
    @Override
    public boolean support(Throwable e) {
        return e instanceof ErrorException;
    }

    @Override
    public void handle(Throwable t, Socket connection) {
        ErrorException error = (ErrorException) t;
        logger.debug(error.getThrowable().toString());
        ErrorCode errorCode = error.getErrorCode();
        HttpStatus status = errorCode.getStatus();

        String body = toJson(errorCode.getCode(), error.getMessage());
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ")
                .append(status.getCode()).append("\r\n");

        sb.append("Date: ")
                .append(DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()))
                .append("\r\n");
        sb.append("Server: be-was\r\n");
        sb.append("Connection: close\r\n");

        sb.append("Content-Type: application/json; charset=utf-8\r\n");
        sb.append("Content-Length: ").append(bodyBytes.length).append("\r\n");
        sb.append("\r\n");

        try (OutputStream out = connection.getOutputStream()) {
            out.write(sb.toString().getBytes(StandardCharsets.US_ASCII));
            out.write(bodyBytes);
            out.flush();
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }

    private String toJson(String code, String message) {
        String safeMsg = message == null ? "" : message
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");

        return "{\"code\":\"" + code + "\",\"message\":\"" + safeMsg + "\"}";
    }
}
