package app.handler;

import app.db.ArticleRepository;
import app.model.Article;
import config.DatabaseConfig;
import exception.ErrorCode;
import exception.ErrorException;
import exception.ServiceException;
import http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.dispatch.argument.MultipartFile;
import web.dispatch.argument.MultipartForm;
import web.filter.authentication.AuthenticationInfo;
import web.handler.DoubleArgHandler;
import web.response.HandlerResponse;
import web.response.RedirectResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CreateArticleWithPost extends DoubleArgHandler<MultipartForm, AuthenticationInfo> {
    private static final Logger log = LoggerFactory.getLogger(CreateArticleWithPost.class);
    private final ArticleRepository articleRepository;

    public CreateArticleWithPost(ArticleRepository articleRepository) {
        super(HttpMethod.POST, "/article/create");
        this.articleRepository = articleRepository;
    }

    @Override
    public HandlerResponse handle(MultipartForm multiform, AuthenticationInfo authInfo) {
        MultipartFile multipartFile = multiform.getFile("file").orElseThrow(
                () -> new ErrorException("No file in multiform"));
        Long userId = authInfo.getUserId().orElseThrow(
                () -> new ServiceException(ErrorCode.UNAUTHORIZED));

        String extension = extractExtension(multipartFile);

        Article saved = articleRepository.save(
                new Article(
                        authInfo.getUserId().get(),
                        multiform.getField("content")
                                .orElse("")));

        Path filePath = Paths.get(DatabaseConfig.ARTICLE_IMG_DIR)
                .resolve(saved.getId().toString() + extension);

        try {
            Files.write(filePath, multipartFile.bytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e){
            log.error(e.fillInStackTrace().toString());
            throw new ErrorException("error");
        }
        log.info("Article id[{}] created by {}({})",
                saved.getId(),
                userId,
                authInfo.getAttribute("nickname"));
        return RedirectResponse.to("/");
    }

    private static String extractExtension(MultipartFile multipartFile) {
        return switch(multipartFile.contentType()){
            case "image/png" -> ".png";
            case "image/jpg" -> ".jpg";
            case "image/jpeg" -> ".jpeg";
            default -> throw new ServiceException(ErrorCode.UNSUPPORTED_IMAGE_TYPE);
        };
    }
}
