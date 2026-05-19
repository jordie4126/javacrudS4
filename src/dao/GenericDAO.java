package dao;

import db.DBConnection;
import reflection.EntityInspector;
import reflection.ObjectHydrator;
import reflection.SqlTypeMapper;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenericDAO<T> {

    /**
     * Insert an object into the specified table. Returns the generated id.
     */
    public int insert(T obj, String tableName) {
        List<Field> fields = EntityInspector.getInsertableFields(obj.getClass());
        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder values = new StringBuilder("VALUES (");

        for (int i = 0; i < fields.size(); i++) {
            sql.append(fields.get(i).getName());
            values.append("?");
            if (i < fields.size() - 1) {
                sql.append(", ");
                values.append(", ");
            }
        }
        sql.append(") ");
        values.append(")");
        sql.append(values);

        try (PreparedStatement pstmt = DBConnection.getInstance().getConnection()
                .prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < fields.size(); i++) {
                Field f = fields.get(i);
                Object value = EntityInspector.getFieldValue(obj, f);
                Object sqlValue = SqlTypeMapper.toSqlValue(value, f.getType());
                pstmt.setObject(i + 1, sqlValue);
            }

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur INSERT dans " + tableName + ": " + sql, e);
        }
    }

    /**
     * Find all records in the specified table.
     */
    public List<T> findAll(Class<T> clazz, String tableName) {
        String sql = "SELECT * FROM " + tableName + " ORDER BY id";
        try (Statement stmt = DBConnection.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return ObjectHydrator.hydrateList(rs, clazz);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SELECT ALL dans " + tableName, e);
        }
    }

    /**
     * Find a record by ID.
     */
    public T findById(Class<T> clazz, String tableName, int id) {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement pstmt = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return ObjectHydrator.hydrate(rs, clazz);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SELECT BY ID dans " + tableName, e);
        }
    }

    /**
     * Update a record by ID.
     */
    public void update(T obj, String tableName, int id) {
        List<Field> fields = EntityInspector.getInsertableFields(obj.getClass());
        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");

        for (int i = 0; i < fields.size(); i++) {
            sql.append(fields.get(i).getName()).append(" = ?");
            if (i < fields.size() - 1) {
                sql.append(", ");
            }
        }
        sql.append(" WHERE id = ?");

        try (PreparedStatement pstmt = DBConnection.getInstance().getConnection().prepareStatement(sql.toString())) {
            for (int i = 0; i < fields.size(); i++) {
                Field f = fields.get(i);
                Object value = EntityInspector.getFieldValue(obj, f);
                Object sqlValue = SqlTypeMapper.toSqlValue(value, f.getType());
                pstmt.setObject(i + 1, sqlValue);
            }
            pstmt.setInt(fields.size() + 1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur UPDATE dans " + tableName, e);
        }
    }

    /**
     * Delete a record by ID.
     */
    public void delete(String tableName, int id) {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement pstmt = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur DELETE dans " + tableName, e);
        }
    }

    /**
     * Find records with a WHERE clause.
     */
    public List<T> findWhere(Class<T> clazz, String tableName, String whereClause, Object... params) {
        String sql = "SELECT * FROM " + tableName + " WHERE " + whereClause + " ORDER BY id";
        try (PreparedStatement pstmt = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                return ObjectHydrator.hydrateList(rs, clazz);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SELECT WHERE dans " + tableName, e);
        }
    }
}
