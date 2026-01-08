package web.dispatch.argument;

import http.request.HttpRequest;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class QueryParametersTest {

    @Test
    void query_params_GET_decode_test() throws UnsupportedEncodingException {
        String value = URLEncoder.encode("한글", "UTF-8");
        HttpRequest request = HttpRequest.from("GET /user/create?test=" + value +  " HTTP/1.1");
        QueryParameters queryParameters = QueryParameters.of(request.getQueryString());

        assertThat(queryParameters.getQueryValue("test").orElse(null)).isEqualTo("한글");
    }
    @Test
    void query_params_GET_parse_and_decode_test(){
        HttpRequest request = HttpRequest.from("GET /user/create?userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net HTTP/1.1");
        QueryParameters queryParameters = QueryParameters.of(request.getQueryString());

        assertThat(queryParameters.getQueryValue("userId").orElse("")).isEqualTo("javajigi");
        assertThat(queryParameters.getQueryValue("password").orElse("")).isEqualTo("password");
        assertThat(queryParameters.getQueryValue("name").orElse("")).isEqualTo("박재성");
        assertThat(queryParameters.getQueryValue("email").orElse("")).isEqualTo("javajigi@slipp.net");
    }
}