package config;

import app.model.User;
import database.TestDao;

import java.util.List;

public class DatabaseConfig {
    public static final String H2_DB_URL = "jdbc:h2:tcp://localhost//Users/apple/h2/testdb";
    public static final String H2_DB_USER = "sa";
    public static final String H2_DB_PASSWORD = "";
}
