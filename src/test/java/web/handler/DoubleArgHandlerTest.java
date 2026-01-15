// src/test/java/web/handler/DoubleArgHandlerTest.java
package web.handler;

import http.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import web.response.HandlerResponse;

import static org.junit.jupiter.api.Assertions.*;

class DoubleArgHandlerTest {

    static class TestDoubleArgHandler extends DoubleArgHandler<String, Integer> {
        protected TestDoubleArgHandler(HttpMethod method, String path) {
            super(method, path);
        }

        @Override
        public HandlerResponse handle(String arg1, Integer arg2) {
            return null;
        }
    }

    @Test
    @DisplayName("getMethod/getPath는 생성자 인자를 그대로 반환한다")
    void getMethodAndPath() {
        var handler = new TestDoubleArgHandler(HttpMethod.POST, "/user/create");

        assertEquals(HttpMethod.POST, handler.getMethod());
        assertEquals("/user/create", handler.getPath());
    }

    @Test
    @DisplayName("checkEndpoint는 method와 path가 모두 일치할 때만 true")
    void checkEndpoint() {
        var handler = new TestDoubleArgHandler(HttpMethod.POST, "/user/create");

        assertTrue(handler.checkEndpoint(HttpMethod.POST, "/user/create"));
        assertFalse(handler.checkEndpoint(HttpMethod.GET, "/user/create"));
        assertFalse(handler.checkEndpoint(HttpMethod.POST, "/user/login"));
        assertFalse(handler.checkEndpoint(HttpMethod.GET, "/user/login"));
    }
}
