package reflection;

import annotation.Champ;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EntityInspector {

    /**
     * Get fields annotated with @Champ on the given class (specific fields only).
     */
    public static List<Field> getChampsSpecifiques(Class<?> clazz) {
        List<Field> champsSpecifiques = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(Champ.class)) {
                f.setAccessible(true);
                champsSpecifiques.add(f);
            }
        }
        return champsSpecifiques;
    }

    /**
     * Get ALL fields from the class and all its superclasses (up to but excluding Object).
     * This includes inherited fields like id, idListIn, dateIn, quantite, prixUnitaire.
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> allFields = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field f : current.getDeclaredFields()) {
                f.setAccessible(true);
                allFields.add(f);
            }
            current = current.getSuperclass();
        }
        return allFields;
    }

    /**
     * Get fields that should be used for INSERT (all fields except 'id' which is auto-generated).
     */
    public static List<Field> getInsertableFields(Class<?> clazz) {
        List<Field> fields = getAllFields(clazz);
        fields.removeIf(f -> f.getName().equals("id"));
        return fields;
    }

    public static Object getFieldValue(Object obj, Field f) {
        try {
            f.setAccessible(true);
            return f.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access field: " + f.getName(), e);
        }
    }

    public static void setFieldValue(Object obj, Field f, Object val) {
        try {
            f.setAccessible(true);
            f.set(obj, val);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot set field: " + f.getName(), e);
        }
    }

    /**
     * Find a field by name across the class hierarchy.
     */
    public static Field getFieldByName(Class<?> clazz, String name) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                Field f = current.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
