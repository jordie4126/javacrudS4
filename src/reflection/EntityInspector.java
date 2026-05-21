package reflection;

import annotation.Champ;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EntityInspector {

    
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
