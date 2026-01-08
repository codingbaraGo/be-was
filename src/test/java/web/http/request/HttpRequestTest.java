package web.http.request;

import http.request.HttpRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class HttpRequestTest {

    @Test
    void query_parameter_parse_test(){
        HttpRequest request = HttpRequest.from("GET /create?userId=user1&nickname=testNick&password=123 HTTP/1.1");
        assertThat(request.getPath()).isEqualTo("/create");
        assertThat(request.getQueryString()).isEqualTo("userId=user1&nickname=testNick&password=123");
    }

}