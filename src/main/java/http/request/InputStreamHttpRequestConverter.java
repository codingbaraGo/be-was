package http.request;

import exception.ErrorException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

public class InputStreamHttpRequestConverter implements HttpRequestConverter{

    @Override
    public HttpRequest parseRequest(Socket connection) {
        try {
            BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());

            String requestLine = readLine(inputStream);
            if (requestLine == null || requestLine.isEmpty()) {
                throw new ErrorException("HttpRequestParseError: Empty request line");
            }

            HttpRequest request = HttpRequest.from(requestLine);
            request.setRequestAddress(connection.getInetAddress());

            readHeaders(inputStream, request);
            readBody(inputStream, request);

            return request;

        } catch (IOException e) {
            throw new ErrorException("HttpRequestParseError: IO Exception", e);
        }
    }

    private void readHeaders(BufferedInputStream in, HttpRequest request) throws IOException {
        while (true) {
            String line = readLine(in);
            if (line == null || line.isEmpty()) {
                break;
            }

            int idx = line.indexOf(':');
            if (idx < 0) {
                throw new ErrorException("HttpRequestHeaderParseError: " + line);
            }

            request.setHeader(
                    line.substring(0, idx).trim(),
                    line.substring(idx + 1).trim()
            );
        }
    }

    private void readBody(BufferedInputStream in, HttpRequest request) throws IOException {
        String contentLength = request.getHeader("Content-Length");
        if (contentLength == null) {
            return; // body 없음
        }

        int length;
        try {
            length = Integer.parseInt(contentLength.trim());
        } catch (NumberFormatException e) {
            throw new ErrorException("Invalid Content-Length: " + contentLength, e);
        }

        if (length <= 0) return;

        byte[] body = new byte[length];
        int offset = 0;

        while (offset < length) {
            int n = in.read(body, offset, length - offset);
            if (n < 0) {
                throw new ErrorException("Unexpected EOF while reading body");
            }
            offset += n;
        }

        request.setBody(body);
    }

    private String readLine(BufferedInputStream in) throws IOException {
        ByteArrayOutputStream lineBuffer = new ByteArrayOutputStream(128);

        int prev = -1;
        while (true) {
            int cur = in.read();
            if (cur == -1) {
                if (lineBuffer.size() == 0) return null;
                return lineBuffer.toString();
            }

            if (prev == '\r' && cur == '\n') {
                byte[] raw = lineBuffer.toByteArray();
                int len = raw.length;
                if (len > 0 && raw[len - 1] == '\r') {
                    return new String(raw, 0, len - 1);
                }
                return lineBuffer.toString();
            }

            lineBuffer.write(cur);
            prev = cur;
        }
    }
}
