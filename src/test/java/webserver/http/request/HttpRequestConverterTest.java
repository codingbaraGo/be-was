package webserver.http.request;

import org.junit.jupiter.api.Test;

import java.net.Socket;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class HttpRequestConverterTest {
    private final HttpRequestConverter converter = new HttpBufferedReaderRequestConverter();

    @Test
    void parse_GET_request_test() throws Exception {
        String raw =
                "GET /hello?name=ta&x=1 HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "User-Agent: junit\r\n" +
                        "\r\n";

        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(
                new ByteArrayInputStream(raw.getBytes(StandardCharsets.ISO_8859_1))
        );

        HttpRequest req = converter.parseRequest(socket);

        assertThat(req.getMethod().name()).isEqualTo("GET");
        assertThat(req.getPath()).isEqualTo("/hello");
        assertThat(req.getQueryValue("name")).isEqualTo("ta");
        assertThat(req.getQueryValue("x")).isEqualTo("1");
        assertThat(req.getHeader("Host")).isEqualTo("localhost");
        assertThat(req.getHeader("User-Agent")).isEqualTo("junit");
    }

    @Test
    void parse_GET_request_test2() throws Exception {
        String raw =
                "GET /index.html HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "User-Agent: junit\r\n" +
                        "\r\n";

        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(
                new ByteArrayInputStream(raw.getBytes(StandardCharsets.ISO_8859_1))
        );

        HttpRequest req = converter.parseRequest(socket);

        assertThat(req.getMethod().name()).isEqualTo("GET");
        assertThat(req.getPath()).isEqualTo("/index.html");
        assertThat(req.getHeader("Host")).isEqualTo("localhost");
        assertThat(req.getHeader("User-Agent")).isEqualTo("junit");
    }
}