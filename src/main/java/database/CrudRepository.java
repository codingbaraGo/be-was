package database;

import config.DatabaseConfig;
import exception.ErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CrudRepository<T> {
    private static final Logger log = LoggerFactory.getLogger(CrudRepository.class);
    private final List<String> resolvedWord = DatabaseConfig.RESOLVED_WORD;
    private final ConnectionManager connectionManager;
    private final Class<T> type;
    private final String tableName;

    public CrudRepository(ConnectionManager connectionManager, Class<T> type) {
        this.connectionManager = connectionManager;
        this.type = type;
        this.tableName = toTableName(type);
    }

    // ========== CREATE ==========
    public T save(T entity) {
        try (Connection conn = connectionManager.getConnection()) {

            Field[] allFields = type.getDeclaredFields();

            StringBuilder sqlBuilder = new StringBuilder();
            StringBuilder placeholder = new StringBuilder();

            sqlBuilder.append("INSERT INTO ").append(tableName).append(" (");

            List<Field> insertFields = new ArrayList<>();

            for (Field field : allFields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if ("id".equals(toColumnName(field.getName()))) {
                    continue;
                }
                sqlBuilder.append(toColumnName(field.getName())).append(", ");
                placeholder.append("?, ");
                insertFields.add(field);
            }

            if (!insertFields.isEmpty()) {
                sqlBuilder.setLength(sqlBuilder.length() - 2);
                placeholder.setLength(placeholder.length() - 2);
            }

            sqlBuilder.append(") VALUES (").append(placeholder).append(")");

            String sql = sqlBuilder.toString();

            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                int index = 1;
                for (Field field : insertFields) {
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    setParameter(pstmt, index++, value);
                }

                pstmt.executeUpdate();

                try (ResultSet resultSet = pstmt.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        long generatedId = resultSet.getLong(1);
                        setId(entity, generatedId);
                    }
                }
            }

            log.info("{} created: {}", tableName, sql);
            return entity;
        } catch (SQLException | IllegalAccessException e) {
            throw new ErrorException("엔티티 저장 중 오류", e);
        }
    }

    // ========== READ ==========
    public Optional<T> findById(Long id) {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    log.info("{} queried: {}", tableName, sql);
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new ErrorException("엔티티 조회 중 오류 (id=" + id + ")", e);
        }
    }

    public List<T> findAll() {
        String sql = "SELECT * FROM " + tableName;

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet resultSet = pstmt.executeQuery()) {

            List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(mapRow(resultSet));
            }
            log.info("{} queried({} results): {}", tableName, result.size(), sql);
            return result;

        } catch (SQLException e) {
            throw new ErrorException("엔티티 전체 조회 중 오류", e);
        }
    }

    public List<T> findByColumn(String columnName, Object value) {
        String sql = "SELECT * FROM " + tableName + " WHERE " + toColumnName(columnName) + " = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setParameter(pstmt, 1, value);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                List<T> result = new ArrayList<>();
                while (resultSet.next()) {
                    result.add(mapRow(resultSet));
                }
                log.info("{} queried({} results): {}", tableName, result.size(), sql);
                return result;
            }

        } catch (SQLException e) {
            throw new ErrorException("엔티티 조회 중 오류 (column=" + toColumnName(columnName) + ", value=" + value + ")", e);
        }
    }

    // ========== UPDATE ==========
    public void update(T entity) {
        Long id = getId(entity);
        if (id == null) {
            throw new ErrorException("id가 null인 엔티티는 업데이트할 수 없습니다.");
        }

        Field[] allFields = type.getDeclaredFields();

        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(tableName).append(" SET ");

        List<Field> updateFields = new ArrayList<>();

        for (Field field : allFields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if ("id".equals(toColumnName(field.getName()))) {
                continue;
            }
            sql.append(toColumnName(field.getName())).append(" = ?, ");
            updateFields.add(field);
        }

        if (!updateFields.isEmpty()) {
            sql.setLength(sql.length() - 2);
        }

        sql.append(" WHERE id = ?");

        String finalSql = sql.toString();

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(finalSql)) {

            int index = 1;

            for (Field field : updateFields) {
                field.setAccessible(true);
                Object value = field.get(entity);
                setParameter(pstmt, index++, value);
            }

            pstmt.setLong(index, id);

            pstmt.executeUpdate();

            log.info("{} updated: {}", tableName, sql);
        } catch (SQLException | IllegalAccessException e) {
            throw new ErrorException("엔티티 업데이트 중 오류", e);
        }
    }

    // ========== private methods ==========
    private void setParameter(PreparedStatement pstmt, int index, Object value) throws SQLException {
        if (value == null) {
            pstmt.setObject(index, null);
            return;
        }

        if (value instanceof Long) {
            pstmt.setLong(index, (Long) value);
        } else if (value instanceof Integer) {
            pstmt.setInt(index, (Integer) value);
        } else if (value instanceof String) {
            pstmt.setString(index, (String) value);
        } else if (value instanceof Boolean) {
            pstmt.setBoolean(index, (Boolean) value);
        } else if (value instanceof LocalDateTime) {
            pstmt.setTimestamp(index, Timestamp.valueOf((LocalDateTime) value));
        } else if (value instanceof java.util.Date) {
            pstmt.setTimestamp(index, new Timestamp(((java.util.Date) value).getTime()));
        } else {
            pstmt.setObject(index, value);
        }
    }

    private void setId(T entity, Long id) {
        try {
            Field idField = type.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ErrorException("id 필드 설정 중 오류", e);
        }
    }

    private Long getId(T entity) {
        try {
            Field idField = type.getDeclaredField("id");
            idField.setAccessible(true);
            Object value = idField.get(entity);
            if (value == null) {
                return null;
            }
            if (value instanceof Long) {
                return (Long) value;
            }
            throw new ErrorException("id 필드 타입이 Long이 아닙니다: " + value.getClass());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ErrorException("id 필드 조회 중 오류", e);
        }
    }

    private T mapRow(ResultSet resultSet) {
        try {
            T instance = type.getDeclaredConstructor().newInstance();

            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                String columnName = toColumnName(field.getName());
                field.setAccessible(true);

                Class<?> fieldType = field.getType();

                Object value;

                if (fieldType == Long.class || fieldType == long.class) {
                    long v = resultSet.getLong(columnName);
                    value = resultSet.wasNull() ? null : v;
                } else if (fieldType == Integer.class || fieldType == int.class) {
                    int v = resultSet.getInt(columnName);
                    value = resultSet.wasNull() ? null : v;
                } else if (fieldType == String.class) {
                    value = resultSet.getString(columnName);
                } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                    boolean v = resultSet.getBoolean(columnName);
                    value = resultSet.wasNull() ? null : v;
                } else if (fieldType == LocalDateTime.class) {
                    Timestamp ts = resultSet.getTimestamp(columnName);
                    value = (ts != null) ? ts.toLocalDateTime() : null;
                } else if (fieldType == java.util.Date.class) {
                    Timestamp ts = resultSet.getTimestamp(columnName);
                    value = (ts != null) ? new java.util.Date(ts.getTime()) : null;
                } else {
                    value = resultSet.getObject(columnName);
                }

                field.set(instance, value);
            }

            return instance;

        } catch (Exception e) {
            throw new ErrorException("ResultSet → 엔티티 매핑 중 오류", e);
        }
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
