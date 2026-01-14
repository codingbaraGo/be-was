package web.dispatch.argument.resolver;

import config.AppConfig;
import exception.ErrorCode;
import exception.ServiceException;
import org.junit.jupiter.api.Test;
import web.dispatch.argument.MultipartFile;
import web.dispatch.argument.MultipartForm;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MultipartFormParserTest {
    private final AppConfig appConfig = new AppConfig();
    private final MultipartFormParser parser = appConfig.multipartFormParser();

    @Test
    void parse_field_and_file_success() {
        // given
        String boundary = "----boundary123";
        byte[] body = (
                "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"title\"\r\n" +
                        "\r\n" +
                        "한글\r\n" +
                        "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"file\"; filename=\"ignored.txt\"\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "\r\n" +
                        "FILE_CONTENT\r\n" +
                        "--" + boundary + "--\r\n"
        ).getBytes(StandardCharsets.UTF_8);

        String contentType = "multipart/form-data; boundary=" + boundary;

        // when
        MultipartForm form = parser.parse(body, contentType);

        // then (field)
        assertThat(form.getFields()).containsKey("title");
        List<String> titleValues = form.getFieldValues("title");
        assertThat(titleValues).hasSize(1);
        assertThat(titleValues.get(0)).isEqualTo("한글");

        // then (file)
        assertThat(form.getFiles()).containsKey("file");
        List<MultipartFile> fileList = form.getFiles("file");
        assertThat(fileList).hasSize(1);

        MultipartFile f = fileList.get(0);
        assertThat(f.fieldName()).isEqualTo("file");
        assertThat(f.contentType()).isEqualTo("text/plain");
        assertThat(f.size()).isEqualTo("FILE_CONTENT".getBytes(StandardCharsets.UTF_8).length);
        assertThat(f.bytes()).isEqualTo("FILE_CONTENT".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void parse_multiple_fields_same_name_accumulates_into_list() {
        // given
        String boundary = "----boundaryTags";
        byte[] body = (
                "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"tag\"\r\n" +
                        "\r\n" +
                        "a\r\n" +
                        "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"tag\"\r\n" +
                        "\r\n" +
                        "b\r\n" +
                        "--" + boundary + "--\r\n"
        ).getBytes(StandardCharsets.UTF_8);

        String contentType = "multipart/form-data; boundary=" + boundary;

        // when
        MultipartForm form = parser.parse(body, contentType);

        // then
        assertThat(form.getFields()).containsKey("tag");
        assertThat(form.getFieldValues("tag")).containsExactly("a", "b");
        assertThat(form.getField("tag")).contains("a"); // first-value convenience
    }

    @Test
    void parse_multiple_files_same_name_accumulates_into_list() {
        // given
        String boundary = "----boundaryFiles";
        byte[] body = (
                "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"files\"; filename=\"a.bin\"\r\n" +
                        "Content-Type: application/octet-stream\r\n" +
                        "\r\n" +
                        "AAA\r\n" +
                        "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"files\"; filename=\"b.bin\"\r\n" +
                        "Content-Type: application/octet-stream\r\n" +
                        "\r\n" +
                        "BBB\r\n" +
                        "--" + boundary + "--\r\n"
        ).getBytes(StandardCharsets.UTF_8);

        String contentType = "multipart/form-data; boundary=" + boundary;

        // when
        MultipartForm form = parser.parse(body, contentType);

        // then
        List<MultipartFile> files = form.getFiles("files");
        assertThat(files).hasSize(2);

        MultipartFile f1 = files.get(0);
        assertThat(f1.fieldName()).isEqualTo("files");
        assertThat(f1.contentType()).isEqualTo("application/octet-stream");
        assertThat(f1.bytes()).isEqualTo("AAA".getBytes(StandardCharsets.UTF_8));

        MultipartFile f2 = files.get(1);
        assertThat(f2.fieldName()).isEqualTo("files");
        assertThat(f2.contentType()).isEqualTo("application/octet-stream");
        assertThat(f2.bytes()).isEqualTo("BBB".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void parse_non_multipart_content_type_throws() {
        // given
        byte[] body = "whatever".getBytes(StandardCharsets.UTF_8);

        // when & then
        assertThatThrownBy(() -> parser.parse(body, "application/json"))
                .isInstanceOf(ServiceException.class)
                .satisfies(ex -> {
                    ServiceException se = (ServiceException) ex;
                    assertThat(se.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT);
                });
    }
}
