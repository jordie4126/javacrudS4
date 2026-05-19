package reflection;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ObjectHydrator {

    /**
     * Hydrate a single object from the current ResultSet row.
     */
    public static <T> T hydrate(ResultSet rs, Class<T> clazz) throws SQLException {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            List<Field> allFields = EntityInspector.getAllFields(clazz);

            // Get available columns from ResultSet
            ResultSetMetaData metaData = rs.getMetaData();
            Set<String> columns = new HashSet<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columns.add(metaData.getColumnName(i).toLowerCase());
            }

            for (Field f : allFields) {
                String colName = f.getName().toLowerCase();
                if (columns.contains(colName)) {
                    Object sqlValue = rs.getObject(f.getName());
                    Object javaValue = SqlTypeMapper.fromSqlValue(sqlValue, f.getType());
                    EntityInspector.setFieldValue(instance, f, javaValue);
                }
            }
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Erreur d'hydratation pour " + clazz.getName(), e);
        }
    }

    /**
     * Hydrate a list of objects from a ResultSet.
     */
    public static <T> List<T> hydrateList(ResultSet rs, Class<T> clazz) throws SQLException {
        List<T> list = new ArrayList<>();
        while (rs.next()) {
            list.add(hydrate(rs, clazz));
        }
        return list;
    }
}
