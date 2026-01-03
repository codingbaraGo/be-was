package webserver.web.handler.response.handler;

import config.VariableConfig;
import webserver.exception.ErrorException;
import webserver.http.HttpStatus;
import webserver.http.response.HttpResponse;
import webserver.web.handler.response.WebHandlerResponse;
import webserver.web.handler.response.staticcontent.StaticContentResponse;

import java.io.*;
import java.util.List;
import java.util.Optional;

public class StaticContentResponseHandler implements WebHandlerResponseHandler{
    private final List<String> staticResourceRoots = VariableConfig.STATIC_RESOURCE_ROOTS;
    @Override
    public boolean supports(WebHandlerResponse response) {
        return response instanceof StaticContentResponse;
    }

    @Override
    public HttpResponse handle(WebHandlerResponse handlerResponse) {
        StaticContentResponse staticResponse = (StaticContentResponse) handlerResponse;
        String path = staticResponse.getPath();

        File file = resolveStaticFile(path)
                .orElseThrow(() -> new ErrorException("Static content path Error"));

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            byte[] body = in.readAllBytes();

            HttpResponse httpResponse = HttpResponse.of(HttpStatus.OK);
            httpResponse.setBody(file, body);
            return httpResponse;

        } catch (IOException e) {
            throw new ErrorException("Static content Read IO-Error");
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
