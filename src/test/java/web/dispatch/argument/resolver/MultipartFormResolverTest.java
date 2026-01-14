package web.dispatch.argument.resolver;

import config.AppConfig;
import exception.ErrorCode;
import exception.ServiceException;
import http.request.HttpRequest;
import org.junit.jupiter.api.Test;
import web.dispatch.argument.MultipartFile;
import web.dispatch.argument.MultipartForm;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MultipartFormResolverTest {
    private final AppConfig appConfig = new AppConfig();
    private final MultipartFormResolver resolver = appConfig.multipartFormResolver();

    @Test
    void resolve_multipart_form_success() {
        // given
        String boundary = "----boundaryResolver";
        byte[] body = (
                "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"nickname\"\r\n" +
                        "\r\n" +
                        "ta\r\n" +
                        "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"avatar\"; filename=\"ignored.png\"\r\n" +
                        "Content-Type: image/png\r\n" +
                        "\r\n" +
                        "PNGDATA\r\n" +
                        "--" + boundary + "--\r\n"
        ).getBytes(StandardCharsets.UTF_8);

        HttpRequest req = HttpRequest.from("POST /upload HTTP/1.1");
        req.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
        req.setHeader("Content-Length", String.valueOf(body.length));
        req.setBody(body);

        // when
        MultipartForm form = resolver.resolve(req);

        // then
        assertThat(form.getField("nickname")).contains("ta");

        List<MultipartFile> avatars = form.getFiles("avatar");
        assertThat(avatars).hasSize(1);

        MultipartFile f = avatars.get(0);
        assertThat(f.fieldName()).isEqualTo("avatar");
        assertThat(f.contentType()).isEqualTo("image/png");
        assertThat(f.bytes()).isEqualTo("PNGDATA".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void resolve_when_not_multipart_throws() {
        // given
        HttpRequest req = HttpRequest.from("POST /upload HTTP/1.1");
        req.setHeader("Content-Type", "application/json");
        req.setBody("{\"a\":1}".getBytes(StandardCharsets.UTF_8));

        // when & then
        assertThatThrownBy(() -> resolver.resolve(req))
                .isInstanceOf(ServiceException.class)
                .satisfies(ex -> {
                    ServiceException se = (ServiceException) ex;
                    assertThat(se.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT);
                });
    }
}
