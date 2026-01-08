package web.dispatch.adapter;

import app.handler.RegisterWithGet;
import config.AppConfig;
import http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import web.handler.WebHandler;
import web.response.StaticViewResponse;
import web.response.HandlerResponse;

import static org.assertj.core.api.Assertions.*;

class SingleArgHandlerAdapterTest {
    AppConfig appConfig = new AppConfig();
    SingleArgHandlerAdapter adapter = new SingleArgHandlerAdapter(appConfig.argumentResolverList());

    @Test
    @DisplayName("SingleArgumentHandlerAdapter<HttpRequest> 성공 테스트")
    void test(){
        HttpRequest request = HttpRequest.from("GET /user/create?userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net HTTP/1.1");
        WebHandler handler = new RegisterWithGet();

        assertThat(adapter.support(handler)).isTrue();

        HandlerResponse response = adapter.handle(request, handler);
        assertThat(response).isNotNull();
        assertThat(response).isInstanceOf(StaticViewResponse.class);
    }

}