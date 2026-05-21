package dao;

import db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class DynamicDAO {

    /**
     * Insert a row into a table. Returns the generated id.
     * @param tableName target table
     * @param data column-name -> value map (excludes 'id' which is auto-generated)
     */
    public int insert(String tableName, Map<String, Object> data) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("No data to insert");
        }

        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder vals = new StringBuilder("VALUES (");
        List<Object> params = new ArrayList<>();

        int i = 0;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (i > 0) { sql.append(", "); vals.append(", "); }
            sql.append(entry.getKey());
            vals.append("?");
            params.add(entry.getValue());
            i++;
        }
        sql.append(") ").append(vals).append(")");

        try (PreparedStatement pstmt = DBConnection.getInstance().getConnection()
                .prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
            for (int j = 0; j < params.size(); j++) {
                pstmt.setObject(j + 1, params.get(j));
            }
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur INSERT dans " + tableName, e);
        }
    }

    public List<Map<String, Object>> findAll(String tableName) {
        String sql = "SELECT * FROM " + tableName + " ORDER BY id";
        try (Statement stmt = DBConnection.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return resultSetToList(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SELECT ALL dans " + tableName, e);
        }
    }

    public List<Map<String, Object>> findWhere(String tableName, String whereClause, Object... params) {
        String sql = "SELECT * FROM " + tableName + " WHERE " + whereClause + " ORDER BY id";
        try (PreparedStatement pstmt = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                return resultSetToList(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SELECT WHERE dans " + tableName, e);
        }
    }

    public List<Map<String, Object>> query(String sql, Object... params) {
        try (PreparedStatement pstmt = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                return resultSetToList(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SELECT custom: " + sql, e);
        }
    }

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
     * Delete rows matching a WHERE clause.
     */
    public void deleteWhere(String tableName, String whereClause, Object... params) {
        String sql = "DELETE FROM " + tableName + " WHERE " + whereClause;
        try (PreparedStatement pstmt = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur DELETE WHERE dans " + tableName, e);
        }
    }

    /**
     * Get a single aggregated value (e.g., SUM, COUNT).
     */
    public double getAggregate(String sql, Object... params) {
        try (PreparedStatement pstmt = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur aggregate: " + sql, e);
        }
        return 0;
    }

    private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= cols; i++) {
                String colName = meta.getColumnName(i);
                Object value = rs.getObject(i);
                // Convert Timestamp to LocalDateTime
                if (value instanceof Timestamp) {
                    value = ((Timestamp) value).toLocalDateTime();
                }
                row.put(colName, value);
            }
            list.add(row);
        }
        return list;
    }
}
