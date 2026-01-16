// src/test/java/web/dispatch/adapter/DoubleArgHandlerAdapterTest.java
package web.dispatch.adapter;

import exception.ErrorException;
import http.HttpMethod;
import http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import web.dispatch.argument.ArgumentResolver;
import web.handler.DoubleArgHandler;
import web.handler.SingleArgHandler;
import web.handler.WebHandler;
import web.response.HandlerResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DoubleArgHandlerAdapterTest {

    // ---- HandlerResponse는 abstract 이므로 테스트용 구현체 필요 ----
    static class TestHandlerResponse extends HandlerResponse {
        protected TestHandlerResponse() {
            super(null); // status는 이 테스트에서 사용하지 않으므로 null로 둬도 됨
        }
    }

    // ---- DoubleArgHandler 테스트용 구현체 ----
    static class TestDoubleHandler extends DoubleArgHandler<String, Integer> {
        String captured1;
        Integer captured2;

        final HandlerResponse toReturn = new TestHandlerResponse();

        protected TestDoubleHandler() {
            super(HttpMethod.POST, "/double");
        }

        @Override
        public HandlerResponse handle(String arg1, Integer arg2) {
            this.captured1 = arg1;
            this.captured2 = arg2;
            return toReturn;
        }
    }

    // ---- support false 용도: SingleArgHandler 구현체 ----
    static class TestSingleHandler extends SingleArgHandler<String> {
        protected TestSingleHandler() {
            super(HttpMethod.POST, "/single");
        }

        @Override
        public HandlerResponse handle(String arg) {
            return new TestHandlerResponse();
        }
    }

    // ---- Resolver: ArgumentResolver는 abstract class이므로 extends로 구현 ----
    static class CountingStringResolver extends ArgumentResolver<String> {
        int calls = 0;
        HttpRequest lastRequest;

        @Override
        public String resolve(HttpRequest request) {
            calls++;
            lastRequest = request;
            return "resolved-string";
        }
    }

    static class CountingIntegerResolver extends ArgumentResolver<Integer> {
        int calls = 0;
        HttpRequest lastRequest;

        @Override
        public Integer resolve(HttpRequest request) {
            calls++;
            lastRequest = request;
            return 99;
        }
    }

    @Test
    @DisplayName("support: DoubleArgHandler 구현체면 true, 아니면 false")
    void support() {
        var adapter = new DoubleArgHandlerAdapter(List.of());

        WebHandler doubleHandler = new TestDoubleHandler();
        WebHandler singleHandler = new TestSingleHandler();

        assertTrue(adapter.support(doubleHandler));
        assertFalse(adapter.support(singleHandler));
    }

    @Test
    @DisplayName("handle: 제네릭 타입에 맞는 resolver로 resolve 후 handler.handle(arg1,arg2)를 호출하고 그 결과를 반환한다")
    void handle_success() {
        // given
        var handler = new TestDoubleHandler();
        var r1 = new CountingStringResolver();
        var r2 = new CountingIntegerResolver();

        // resolver 순서가 바뀌어도 타입 매칭으로 찾아야 함
        var adapter = new DoubleArgHandlerAdapter(List.of(r2, r1));

        HttpRequest request = HttpRequest.from("POST /double HTTP/1.1");

        // when
        HandlerResponse result = adapter.handle(request, handler);

        // then: 반환값은 handler가 준 객체 그대로
        assertSame(handler.toReturn, result);

        // then: resolver는 각각 1회 호출
        assertEquals(1, r1.calls);
        assertEquals(1, r2.calls);

        // then: 동일 request가 resolver로 전달
        assertSame(request, r1.lastRequest);
        assertSame(request, r2.lastRequest);

        // then: handler는 resolve 결과로 호출됨
        assertEquals("resolved-string", handler.captured1);
        assertEquals(99, handler.captured2);
    }

    @Test
    @DisplayName("handle: 첫 번째 인자(String) resolver가 없으면 ErrorException(resolver1 메시지)")
    void handle_noResolver1() {
        var handler = new TestDoubleHandler();
        var onlyInteger = new CountingIntegerResolver();

        var adapter = new DoubleArgHandlerAdapter(List.of(onlyInteger));
        HttpRequest request = HttpRequest.from("POST /double HTTP/1.1");

        ErrorException ex = assertThrows(ErrorException.class, () -> adapter.handle(request, handler));
        assertEquals("HandlerAdapterError: No argument resolver1 supported.", ex.getMessage());
    }

    @Test
    @DisplayName("handle: 두 번째 인자(Integer) resolver가 없으면 ErrorException(resolver2 메시지)")
    void handle_noResolver2() {
        var handler = new TestDoubleHandler();
        var onlyString = new CountingStringResolver();

        var adapter = new DoubleArgHandlerAdapter(List.of(onlyString));
        HttpRequest request = HttpRequest.from("POST /double HTTP/1.1");

        ErrorException ex = assertThrows(ErrorException.class, () -> adapter.handle(request, handler));
        assertEquals("HandlerAdapterError: No argument resolver2 supported.", ex.getMessage());
    }
}
