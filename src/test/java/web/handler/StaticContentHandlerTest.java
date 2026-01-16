package web.handler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import http.HttpMethod;

class StaticContentHandlerTest {
    private final StaticContentHandler handler = new StaticContentHandler();
    @Test
    void support_test(){
        Assertions.assertThat(handler.checkEndpoint(HttpMethod.GET, "/main.css")).isTrue();
    }

}