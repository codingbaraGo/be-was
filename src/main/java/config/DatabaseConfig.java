package config;

import app.model.Article;
import app.model.User;
import java.util.List;

public class DatabaseConfig {
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

    public static final boolean CREATE_TABLES = false;
    public static final boolean DROP_IF_EXISTS = false;

    public void config(){
        DdlGenerator ddlGenerator = appConfig.ddlGenerator();
        if(CREATE_TABLES) ddlGenerator.generateTables();
    }
}
