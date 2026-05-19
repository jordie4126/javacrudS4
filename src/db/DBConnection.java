package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    private static final String URL = "jdbc:postgresql://localhost:5432/gene";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    private DBConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion PostgreSQL etablie.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver PostgreSQL non trouve", e);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion a la base de donnees", e);
        }
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de reconnexion", e);
        }
        return connection;
    }

    public void initMetaTable() {
        try (Statement stmt = getConnection().createStatement()) {
            // Table registre des entites
            stmt.execute("CREATE TABLE IF NOT EXISTS entite_meta (" +
                    "id SERIAL PRIMARY KEY, " +
                    "nom VARCHAR(100) UNIQUE NOT NULL, " +
                    "methode_stock VARCHAR(10) NOT NULL, " +
                    "date_creation TIMESTAMP DEFAULT NOW())");
            System.out.println("Table entite_meta verifiee/creee.");

            // Table des champs dynamiques
            stmt.execute("CREATE TABLE IF NOT EXISTS entite_champs (" +
                    "id SERIAL PRIMARY KEY, " +
                    "nom_entite VARCHAR(100) NOT NULL, " +
                    "nom_champ VARCHAR(100) NOT NULL, " +
                    "type_java VARCHAR(50) NOT NULL, " +
                    "label VARCHAR(255) NOT NULL, " +
                    "ordre INT DEFAULT 0)");
            System.out.println("Table entite_champs verifiee/creee.");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur creation tables systeme", e);
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
