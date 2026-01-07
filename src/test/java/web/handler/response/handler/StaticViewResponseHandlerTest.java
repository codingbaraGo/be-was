package web.handler.response.handler;

import http.HttpStatus;
import org.junit.jupiter.api.*;
import web.response.HandlerResponse;
import web.renderer.StaticViewRenderer;
import web.response.StaticViewResponse;

import java.io.IOException;
import java.nio.file.*;

import static org.assertj.core.api.Assertions.*;

class StaticViewResponseHandlerTest {

    private StaticViewRenderer handler;
    private Path resourceRoot;

    @BeforeEach
    void setUp() {
        handler = new StaticViewRenderer();
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
        HandlerResponse response = StaticViewResponse.of("/index.html");
        assertThat(handler.supports(response)).isTrue();
    }

    @Test
    void supports_false_when_response_is_not_StaticContentResponse() {
        HandlerResponse other = new HandlerResponse(HttpStatus.OK) {};
        assertThat(handler.supports(other)).isFalse();
    }
}
