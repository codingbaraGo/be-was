package web.dispatch.argument.resolver;

import http.request.HttpRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import web.dispatch.argument.QueryParameters;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;

class QueryParamsResolverTest {
    QueryParamsResolver resolver = new QueryParamsResolver();

    @Test
    void query_params_resolver_parse_POST_test() throws UnsupportedEncodingException {
        HttpRequest request = HttpRequest.from("POST /user/create HTTP/1.1");
        request.setHeader("Host", "localhost:8080");
        request.setHeader("Connection", "keep-alive");
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setHeader("Content-Length","67");
        request.setBody("userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net".getBytes("UTF-8"));

        QueryParameters queryParameters = resolver.resolve(request);
        assertThat(queryParameters.getQueryValue("userId").orElse(null)).isEqualTo("javajigi");
        assertThat(queryParameters.getQueryValue("password").orElse(null)).isEqualTo("password");
        assertThat(queryParameters.getQueryValue("name").orElse(null)).isEqualTo("박재성");
        assertThat(queryParameters.getQueryValue("email").orElse(null)).isEqualTo("javajigi@slipp.net");
    }

    @Test
    void query_params_resolver_parse_GET_test(){
        HttpRequest request = HttpRequest.from("GET /user/create?userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net HTTP/1.1");
        QueryParameters queryParameters = resolver.resolve(request);

        assertThat(queryParameters.getQueryValue("userId").orElse("")).isEqualTo("javajigi");
        assertThat(queryParameters.getQueryValue("password").orElse("")).isEqualTo("password");
        assertThat(queryParameters.getQueryValue("name").orElse("")).isEqualTo("박재성");
        assertThat(queryParameters.getQueryValue("email").orElse("")).isEqualTo("javajigi@slipp.net");
    }
}