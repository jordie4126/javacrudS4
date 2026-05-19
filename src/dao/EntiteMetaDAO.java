package dao;

import db.DBConnection;
import model.EntiteChamp;
import model.EntiteMeta;
import model.StockMethode;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntiteMetaDAO {

    public void saveEntite(String nom, StockMethode methode) {
        String sql = "INSERT INTO entite_meta (nom, methode_stock) VALUES (?, ?)";
        try (PreparedStatement pstmt = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            pstmt.setString(1, nom);
            pstmt.setString(2, methode.name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur sauvegarde entite_meta: " + nom, e);
        }
    }

    public void saveChamps(String nomEntite, List<EntiteChamp> champs) {
        String sql = "INSERT INTO entite_champs (nom_entite, nom_champ, type_java, label, ordre) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            for (EntiteChamp ch : champs) {
                pstmt.setString(1, nomEntite);
                pstmt.setString(2, ch.getNomChamp());
                pstmt.setString(3, ch.getTypeJava());
                pstmt.setString(4, ch.getLabel());
                pstmt.setInt(5, ch.getOrdre());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur sauvegarde champs pour: " + nomEntite, e);
        }
    }

    public List<EntiteChamp> getChamps(String nomEntite) {
        String sql = "SELECT * FROM entite_champs WHERE nom_entite = ? ORDER BY ordre";
        List<EntiteChamp> list = new ArrayList<>();
        try (PreparedStatement pstmt = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            pstmt.setString(1, nomEntite);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    EntiteChamp ch = new EntiteChamp();
                    ch.setId(rs.getInt("id"));
                    ch.setNomEntite(rs.getString("nom_entite"));
                    ch.setNomChamp(rs.getString("nom_champ"));
                    ch.setTypeJava(rs.getString("type_java"));
                    ch.setLabel(rs.getString("label"));
                    ch.setOrdre(rs.getInt("ordre"));
                    list.add(ch);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur chargement champs pour: " + nomEntite, e);
        }
        return list;
    }

    public List<EntiteMeta> loadAllEntites() {
        String sql = "SELECT * FROM entite_meta ORDER BY date_creation";
        List<EntiteMeta> list = new ArrayList<>();
        try (Statement stmt = DBConnection.getInstance().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                EntiteMeta meta = new EntiteMeta();
                meta.setId(rs.getInt("id"));
                meta.setNom(rs.getString("nom"));
                meta.setMethodeStock(StockMethode.valueOf(rs.getString("methode_stock")));
                Timestamp ts = rs.getTimestamp("date_creation");
                if (ts != null) meta.setDateCreation(ts.toLocalDateTime());
                list.add(meta);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur chargement entite_meta", e);
        }
        return list;
    }

    public EntiteMeta findByNom(String nom) {
        String sql = "SELECT * FROM entite_meta WHERE nom = ?";
        try (PreparedStatement pstmt = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            pstmt.setString(1, nom);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    EntiteMeta meta = new EntiteMeta();
                    meta.setId(rs.getInt("id"));
                    meta.setNom(rs.getString("nom"));
                    meta.setMethodeStock(StockMethode.valueOf(rs.getString("methode_stock")));
                    Timestamp ts = rs.getTimestamp("date_creation");
                    if (ts != null) meta.setDateCreation(ts.toLocalDateTime());
                    return meta;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche entite_meta: " + nom, e);
        }
        return null;
    }

    public void deleteEntite(String nom) {
        SchemaGenerator.dropTablesForEntite(nom);
        try (Statement stmt = DBConnection.getInstance().getConnection().createStatement()) {
            stmt.execute("DELETE FROM entite_champs WHERE nom_entite = '" + nom + "'");
            stmt.execute("DELETE FROM entite_meta WHERE nom = '" + nom + "'");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression entite: " + nom, e);
        }
    }
}
