package webserver.web.handler.response.handler;

import org.junit.jupiter.api.*;
import webserver.http.HttpStatus;
import webserver.http.response.HttpResponse;
import webserver.web.handler.response.WebHandlerResponse;
import webserver.web.handler.response.staticcontent.StaticContentResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static org.assertj.core.api.Assertions.*;

class StaticContentResponseHandlerTest {

    private StaticContentResponseHandler handler;
    private Path resourceRoot;

    @BeforeEach
    void setUp() {
        handler = new StaticContentResponseHandler();
        resourceRoot = Paths.get(System.getProperty("user.dir"))
                .resolve("src/main/java/resource");
    }

    @AfterEach
    void tearDown() throws IOException {
        Path index = resourceRoot.resolve("index.html");
        if (Files.exists(index)) {
            Files.delete(index);
        }
    }

    @Test
    void supports_true_when_response_is_StaticContentResponse() {
        WebHandlerResponse response = StaticContentResponse.of("/index.html");
        assertThat(handler.supports(response)).isTrue();
    }

    @Test
    void supports_false_when_response_is_not_StaticContentResponse() {
        WebHandlerResponse other = new WebHandlerResponse() {};
        assertThat(handler.supports(other)).isFalse();
    }

    @Test
    void handle_returns_404_when_file_not_found() {
        StaticContentResponse response =
                StaticContentResponse.of("/no_such_file.html");

        HttpResponse httpResponse = handler.handle(response);

        assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(httpResponse.getHeader("content-length"))
                .containsExactly("0");
    }

    @Test
    void handle_reads_index_html_file_successfully() throws IOException {
        // given
        Files.createDirectories(resourceRoot);

        String content = "<html><body>hello index</body></html>";
        Path index = resourceRoot.resolve("index.html");

        Files.writeString(index, content, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);

        StaticContentResponse response =
                StaticContentResponse.of("/index.html");

        // when
        HttpResponse httpResponse = handler.handle(response);

        // then
        assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.OK);

        byte[] body = httpResponse.getBody();
        assertThat(body).isNotNull();
        assertThat(new String(body, StandardCharsets.UTF_8))
                .isEqualTo(content);

        assertThat(httpResponse.getHeader("content-length"))
                .containsExactly(String.valueOf(body.length));

        assertThat(httpResponse.getHeader("content-type").get(0))
                .startsWith("text/html");
    }
}
