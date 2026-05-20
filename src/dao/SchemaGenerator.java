package dao;

import db.DBConnection;
import model.EntiteChamp;

import java.sql.Statement;
import java.util.List;

public class SchemaGenerator {

    /**
     * Create all 4 tables for a given entity using dynamic field definitions.
     */
    public static void createTablesForEntite(String nomEntite, List<EntiteChamp> champs) {
        try (Statement stmt = DBConnection.getInstance().getConnection().createStatement()) {
            // 1. _ListIn
                String listInSql = "CREATE TABLE IF NOT EXISTS " + nomEntite + "_ListIn (" +
                    "id SERIAL PRIMARY KEY, " +
                    "dateListIn TIMESTAMP, " +
                    "quantiteTotale DOUBLE PRECISION, " +
                    "prixMoyen DOUBLE PRECISION)";
            stmt.execute(listInSql);
            System.out.println("Table " + nomEntite + "_ListIn creee.");

            // 2. _In with dynamic columns
            StringBuilder inSql = new StringBuilder();
            inSql.append("CREATE TABLE IF NOT EXISTS ").append(nomEntite).append("_In (");
            inSql.append("id SERIAL PRIMARY KEY, ");
            inSql.append("idListIn INT REFERENCES ").append(nomEntite).append("_ListIn(id), ");
            inSql.append("dateIn TIMESTAMP, ");
            inSql.append("quantite DOUBLE PRECISION, ");
            inSql.append("prixUnitaire DOUBLE PRECISION");

            for (EntiteChamp ch : champs) {
                inSql.append(", ").append(ch.getNomChamp()).append(" ").append(ch.getSqlType());
            }
            inSql.append(")");
            stmt.execute(inSql.toString());
            System.out.println("Table " + nomEntite + "_In creee.");

            // 3. _ListOut
                String listOutSql = "CREATE TABLE IF NOT EXISTS " + nomEntite + "_ListOut (" +
                    "id SERIAL PRIMARY KEY, " +
                    "dateListOut TIMESTAMP, " +
                    "quantiteTotale DOUBLE PRECISION, " +
                    "prixMoyenUnitaire DOUBLE PRECISION)";
            stmt.execute(listOutSql);
            System.out.println("Table " + nomEntite + "_ListOut creee.");

            // 4. _Out
                String outSql = "CREATE TABLE IF NOT EXISTS " + nomEntite + "_Out (" +
                    "id SERIAL PRIMARY KEY, " +
                    "idListOut INT REFERENCES " + nomEntite + "_ListOut(id), " +
                    "idListInSource INT REFERENCES " + nomEntite + "_ListIn(id), " +
                    "dateOut TIMESTAMP, " +
                    "quantite DOUBLE PRECISION, " +
                    "prixUnitaire DOUBLE PRECISION)";
            stmt.execute(outSql);
            System.out.println("Table " + nomEntite + "_Out creee.");

        } catch (Exception e) {
            throw new RuntimeException("Erreur creation tables pour " + nomEntite, e);
        }
    }

    /**
     * Drop all 4 tables for a given entity.
     */
    public static void dropTablesForEntite(String nomEntite) {
        try (Statement stmt = DBConnection.getInstance().getConnection().createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS " + nomEntite + "_Out CASCADE");
            stmt.execute("DROP TABLE IF EXISTS " + nomEntite + "_ListOut CASCADE");
            stmt.execute("DROP TABLE IF EXISTS " + nomEntite + "_In CASCADE");
            stmt.execute("DROP TABLE IF EXISTS " + nomEntite + "_ListIn CASCADE");
            System.out.println("Tables pour " + nomEntite + " supprimees.");
        } catch (Exception e) {
            throw new RuntimeException("Erreur suppression tables pour " + nomEntite, e);
        }
    }
}
