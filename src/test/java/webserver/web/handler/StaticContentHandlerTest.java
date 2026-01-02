package webserver.web.handler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import webserver.http.HttpMethod;
import webserver.http.request.HttpRequest;

import static org.junit.jupiter.api.Assertions.*;

class StaticContentHandlerTest {
    private final StaticContentHandler handler = new StaticContentHandler();
    @Test
    void support_test(){
        Assertions.assertThat(handler.checkEndpoint(HttpMethod.GET, "/index.html")).isTrue();
    }

}