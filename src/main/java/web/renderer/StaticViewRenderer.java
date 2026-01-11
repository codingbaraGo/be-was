package web.renderer;

import config.VariableConfig;
import exception.ErrorException;
import http.HttpStatus;
import http.response.HttpResponse;
import web.response.HandlerResponse;
import web.response.StaticViewResponse;

import java.io.*;
import java.util.List;
import java.util.Optional;

public class StaticViewRenderer implements HttpResponseRenderer {
    private final List<String> staticResourceRoots = VariableConfig.STATIC_RESOURCE_ROOTS;
    @Override
    public boolean supports(HandlerResponse response) {
        return response instanceof StaticViewResponse;
    }

    @Override
    public HttpResponse handle(HttpResponse httpResponse, HandlerResponse handlerResponse) {
        StaticViewResponse staticResponse = (StaticViewResponse) handlerResponse;
        String path = staticResponse.getPath();

        File file = resolveStaticFile(path)
                .orElseThrow(() -> new ErrorException("Static view path Error"));

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            byte[] body = in.readAllBytes();

            httpResponse.setStatus(handlerResponse.getStatus());
            httpResponse.setBody(file, body);
            handlerResponse.getCookies().forEach(cookie->httpResponse.addHeader("Set-Cookie", cookie));
            return httpResponse;

        } catch (IOException e) {
            throw new ErrorException("Static view Read IO-Error");
        }
    }

    private Optional<File> resolveStaticFile(String path){
        for(String root : staticResourceRoots){
            File requestedFile = new File(root + path);
            if(requestedFile.exists() && requestedFile.isFile()) return Optional.of(requestedFile);

            String indexFilePath = path + (path.endsWith("/") ? "index.html" : "/index.html");
            File indexFile = new File(root + indexFilePath);
            if(indexFile.exists() && indexFile.isFile()) return Optional.of(indexFile);
        }
        return Optional.empty();
    }
}
