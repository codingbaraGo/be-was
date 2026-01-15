package database;

import config.DatabaseConfig;
import exception.ErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2DbManager implements ConnectionManager{
    private static final String DB_URL = DatabaseConfig.H2_DB_URL;
    private static final String DB_USER = DatabaseConfig.H2_DB_USER;
    private static final String DB_PASSWORD = DatabaseConfig.H2_DB_PASSWORD;
    private static final Logger log = LoggerFactory.getLogger(H2DbManager.class);

    @Override
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e){
            log.info(e.fillInStackTrace().toString());
            log.info(e.getSQLState());
            throw new ErrorException("DB error");
        }
    }
}
