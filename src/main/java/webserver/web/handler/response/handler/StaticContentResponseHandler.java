package webserver.web.handler.response.handler;

import webserver.exception.ErrorException;
import webserver.http.HttpStatus;
import webserver.http.response.HttpResponse;
import webserver.web.handler.response.WebHandlerResponse;
import webserver.web.handler.response.staticcontent.StaticContentResponse;

import java.io.*;
import java.net.URLConnection;

public class StaticContentResponseHandler implements WebHandlerResponseHandler{
    private final String DEFAULT_PATH = "./src/main/resources/static";
    @Override
    public boolean supports(WebHandlerResponse response) {
        return response instanceof StaticContentResponse;
    }

    @Override
    public HttpResponse handle(WebHandlerResponse handlerResponse) {
        StaticContentResponse staticResponse = (StaticContentResponse) handlerResponse;

        String path = staticResponse.getPath();
        File file;
        File file1 = new File(DEFAULT_PATH + path);
        File file2 = new File(DEFAULT_PATH + path + "/index.html");

        if(file1.exists() && file1.isFile()) file = file1;
        else file = file2;

        //TODO: Prevent path traversal attack
        if (!file.exists() || !file.isFile()) {
            throw new ErrorException("Static content path Error");
        }

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            byte[] body = in.readAllBytes();

            HttpResponse httpResponse = HttpResponse.of(HttpStatus.OK);
            httpResponse.setBody(body);

            //TODO: response의 body관련 헤더를 일관성 있게 한 포인트에서 처리하도록 수정
            String contentType = guessContentType(file);
            httpResponse.setHeader("Content-Type", contentType);
//            httpResponse.setHeader("Content-Length", String.valueOf(body.length));
            return httpResponse;

        } catch (IOException e) {
            throw new ErrorException("Static content Read IO-Error");
        }
    }

    private String guessContentType(File file) {
        String byName = URLConnection.guessContentTypeFromName(file.getName());
        if (byName != null) return byName;

        String name = file.getName().toLowerCase();
        if (name.endsWith(".html") || name.endsWith(".htm")) return "text/html; charset=utf-8";
        if (name.endsWith(".css")) return "text/css; charset=utf-8";
        if (name.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (name.endsWith(".json")) return "application/json; charset=utf-8";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".gif")) return "image/gif";
        if (name.endsWith(".svg")) return "image/svg+xml";
        if (name.endsWith(".txt")) return "text/plain; charset=utf-8";

        return "application/octet-stream";
    }
}
