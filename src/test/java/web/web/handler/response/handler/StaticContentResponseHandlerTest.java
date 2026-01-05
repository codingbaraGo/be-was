package web.web.handler.response.handler;

import org.junit.jupiter.api.*;
import web.web.handler.response.WebHandlerResponse;
import web.web.handler.response.staticcontent.StaticContentResponse;

import java.io.IOException;
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
}
