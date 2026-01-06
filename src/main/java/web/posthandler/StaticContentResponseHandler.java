package web.posthandler;

import config.VariableConfig;
import exception.ErrorException;
import http.HttpStatus;
import http.response.HttpResponse;
import web.response.WebHandlerResponse;
import web.response.StaticContentResponse;

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

            HttpResponse httpResponse = HttpResponse.of(handlerResponse.getStatus());
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
