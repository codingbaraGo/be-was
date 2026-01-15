package config;

import app.model.Article;
import app.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

public class DatabaseConfig {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);
    private final AppConfig appConfig = new AppConfig();

    public static final List<Class<?>> ENTITY_CLASSES = List.of(
            User.class,
            Article.class
    );

    public static final List<String> RESOLVED_WORD = List.of(
            "user"
    );

    public static final String H2_DB_URL = "jdbc:h2:tcp://localhost//Users/apple/h2/testdb";
    public static final String H2_DB_USER = "sa";
    public static final String H2_DB_PASSWORD = "";

    public static final String ARTICLE_IMG_DIR = "./src/main/resources/static/article/img";

    public static final boolean CREATE_TABLES = false;
    public static final boolean DROP_IF_EXISTS = false;

    public void config(){
        DdlGenerator ddlGenerator = appConfig.ddlGenerator();
        if(CREATE_TABLES) {
            ddlGenerator.generateTables();
        }
        if(DROP_IF_EXISTS){
            resetDir(ARTICLE_IMG_DIR);
        }
    }

    public void resetDir(String dirPath){
        Path dir = Paths.get(dirPath);

        try {
            if (Files.exists(dir)) {
                Files.walk(dir)
                        .sorted(Comparator.reverseOrder()) // 자식부터 삭제
                        .forEach(p -> {
                            try {
                                Files.deleteIfExists(p);
                            } catch (IOException e) {
                                log.error(e.fillInStackTrace().toString());
                            }
                        });
            }
            Files.createDirectories(dir);
        } catch (IOException e){
            log.error(e.fillInStackTrace().toString());
        }
    }
}