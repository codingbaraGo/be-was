package config;

import database.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

public class DdlGenerator {
    private static final Logger log = LoggerFactory.getLogger(DdlGenerator.class);
    private final List<String> resolvedWord = DatabaseConfig.RESOLVED_WORD;
    private final ConnectionManager connectionManager;
    private final boolean dropIfExists = DatabaseConfig.DROP_IF_EXISTS;
    private final List<Class<?>> entityClasses = DatabaseConfig.ENTITY_CLASSES;

    public DdlGenerator(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void generateTables() {
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement()) {

            for (Class<?> entityClass : entityClasses) {
                String tableName = toTableName(entityClass);
                String ddl = buildDdlForEntity(entityClass, tableName);
                log.info("DDL for {}:\n{}", tableName, ddl);
                stmt.execute(ddl);
            }

        } catch (SQLException e) {
            throw new RuntimeException("DDL 생성/실행 중 오류", e);
        }
    }

    private String buildDdlForEntity(Class<?> entityClass, String tableName) {
        StringBuilder ddl = new StringBuilder();

        if (dropIfExists) {
            ddl.append("DROP TABLE IF EXISTS ").append(tableName).append(";\n");
        }

        ddl.append("CREATE TABLE ").append(tableName).append(" (\n");

        Field[] fields = entityClass.getDeclaredFields();

        boolean firstColumn = true;

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            String columnName = toColumnName(field.getName());
            Class<?> fieldType = field.getType();

            if (!firstColumn) {
                ddl.append(",\n");
            }
            firstColumn = false;

            if ("id".equals(columnName) && (fieldType == Long.class || fieldType == long.class)) {
                ddl.append("    id BIGINT AUTO_INCREMENT PRIMARY KEY");
            } else {
                String sqlType = toSqlType(fieldType);
                ddl.append("    ").append(columnName).append(" ").append(sqlType);
            }
        }

        ddl.append("\n);");

        return ddl.toString();
    }

    private String toSqlType(Class<?> fieldType) {
        if (fieldType == Long.class || fieldType == long.class) {
            return "BIGINT";
        } else if (fieldType == Integer.class || fieldType == int.class) {
            return "INT";
        } else if (fieldType == String.class) {
            return "VARCHAR(255)";
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return "BOOLEAN";
        } else if (fieldType == LocalDateTime.class ||
                fieldType == java.util.Date.class ||
                fieldType == java.sql.Timestamp.class) {
            return "TIMESTAMP";
        } else if (fieldType == Double.class || fieldType == double.class) {
            return "DOUBLE";
        } else if (fieldType == Float.class || fieldType == float.class) {
            return "FLOAT";
        }
        return "VARCHAR(255)";
    }

    private String toTableName(Class<?> clazz) {
        String name = clazz.getSimpleName().toLowerCase();

        for(String word : resolvedWord){
            if(word.equals(name))
                return "`" + word + "`";
        }
        return name;
    }

    private String toColumnName(String str){
        String snake = str.replaceAll("(?<!^)([A-Z])", "_$1");
        return snake.toLowerCase();
    }
}
