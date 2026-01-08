package web.http.response;

import http.response.HttpResponseBufferedStreamConverter;
import http.response.HttpResponse;
import http.response.HttpResponseConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class HttpResponseConverterTest {
    private final HttpResponseConverter converter = new HttpResponseBufferedStreamConverter();

    @Test
    void write_response_to_socket_test() throws Exception {
        Socket socket = mock(Socket.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(out);

        HttpResponse res = HttpResponse.of(HttpStatus.OK);
        res.setHeader("Content-Type", "text/plain; charset=utf-8");
        res.setBody("PONG".getBytes(StandardCharsets.UTF_8));

        converter.sendResponse(res, socket);

        String raw = out.toString(StandardCharsets.ISO_8859_1);

        assertThat(raw).startsWith("HTTP/1.1 200");
        assertThat(raw).contains("\r\n\r\n");
        assertThat(raw).containsIgnoringCase("Content-Length: 4");
        assertThat(raw).endsWith("PONG");
    }

}