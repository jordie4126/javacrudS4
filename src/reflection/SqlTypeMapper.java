package reflection;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class SqlTypeMapper {

    private static final Map<Class<?>, String> typeMap = new HashMap<>();

    static {
        typeMap.put(String.class, "VARCHAR(255)");
        typeMap.put(int.class, "INT");
        typeMap.put(Integer.class, "INT");
        typeMap.put(long.class, "BIGINT");
        typeMap.put(Long.class, "BIGINT");
        typeMap.put(double.class, "DOUBLE PRECISION");
        typeMap.put(Double.class, "DOUBLE PRECISION");
        typeMap.put(float.class, "REAL");
        typeMap.put(Float.class, "REAL");
        typeMap.put(boolean.class, "BOOLEAN");
        typeMap.put(Boolean.class, "BOOLEAN");
        typeMap.put(LocalDateTime.class, "TIMESTAMP");
    }

    public static String toSqlType(Class<?> javaType) {
        String sqlType = typeMap.get(javaType);
        if (sqlType == null) {
            throw new IllegalArgumentException("Type Java non supporté pour SQL: " + javaType.getName());
        }
        return sqlType;
    }

    /**
     * Convert a Java value to a SQL-compatible representation for PreparedStatement.
     */
    public static Object toSqlValue(Object value, Class<?> fieldType) {
        if (value == null) return null;
        if (fieldType == LocalDateTime.class) {
            return java.sql.Timestamp.valueOf((LocalDateTime) value);
        }
        return value;
    }

    /**
     * Convert a SQL ResultSet value back to the expected Java type.
     */
    public static Object fromSqlValue(Object sqlValue, Class<?> fieldType) {
        if (sqlValue == null) return getDefaultValue(fieldType);
        if (fieldType == LocalDateTime.class && sqlValue instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) sqlValue).toLocalDateTime();
        }
        if (fieldType == int.class || fieldType == Integer.class) {
            return ((Number) sqlValue).intValue();
        }
        if (fieldType == long.class || fieldType == Long.class) {
            return ((Number) sqlValue).longValue();
        }
        if (fieldType == double.class || fieldType == Double.class) {
            return ((Number) sqlValue).doubleValue();
        }
        if (fieldType == float.class || fieldType == Float.class) {
            return ((Number) sqlValue).floatValue();
        }
        if (fieldType == boolean.class || fieldType == Boolean.class) {
            return (Boolean) sqlValue;
        }
        return sqlValue;
    }

    private static Object getDefaultValue(Class<?> type) {
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == double.class) return 0.0;
        if (type == float.class) return 0.0f;
        if (type == boolean.class) return false;
        return null;
    }
}
