package service;

import dao.DynamicDAO;
import dao.EntiteMetaDAO;
import dao.GenericDAO;
import dao.SchemaGenerator;
import model.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StockService {

    private DynamicDAO dynamicDAO = new DynamicDAO();
    private GenericDAO<ListIn> listInDAO = new GenericDAO<>();
    private GenericDAO<ListOut> listOutDAO = new GenericDAO<>();
    private GenericDAO<Out> outDAO = new GenericDAO<>();
    private EntiteMetaDAO entiteMetaDAO = new EntiteMetaDAO();

    /**
     * Register a new entity type: save metadata, save field definitions, create tables.
     */
    public void enregistrerEntite(String nom, StockMethode methode, List<EntiteChamp> champs) {
        entiteMetaDAO.saveEntite(nom, methode);
        entiteMetaDAO.saveChamps(nom, champs);
        SchemaGenerator.createTablesForEntite(nom, champs);
    }

    /**
     * Create a ListIn entry. Returns the generated id.
     */
    public int creerListIn(String nomEntite, double quantite, double prixMoyen) {
        ListIn listIn = new ListIn();
        listIn.setDateListIn(LocalDateTime.now());
        listIn.setQuantiteTotale(quantite);
        listIn.setPrixMoyen(prixMoyen);
        return listInDAO.insert(listIn, nomEntite + "_ListIn");
    }

    /**
     * Insert an In record (dynamic fields).
     */
    public int entree(String nomEntite, Map<String, Object> data) {
        return dynamicDAO.insert(nomEntite + "_In", data);
    }

    /**
     * Perform a stock exit using the configured strategy.
     */
    public void sortie(String nomEntite, double quantite) {
        EntiteMeta meta = entiteMetaDAO.findByNom(nomEntite);
        if (meta == null) {
            throw new IllegalArgumentException("Entite non trouvee: " + nomEntite);
        }

        // Get available stock
        List<ListIn> stockDisponible = getStockListIn(nomEntite);

        // Subtract already-consumed quantities
        for (ListIn listIn : stockDisponible) {
            double consumed = getConsumedQuantity(nomEntite, listIn.getId());
            listIn.setQuantiteTotale(listIn.getQuantiteTotale() - consumed);
        }

        // Select strategy
        SortieStrategy strategy = getStrategy(meta.getMethodeStock());
        List<Out> sorties = strategy.calculerSorties(stockDisponible, quantite);

        // Calculate weighted average exit price
        double totalValeur = 0;
        double totalQuantite = 0;
        for (Out out : sorties) {
            totalValeur += out.getQuantite() * out.getPrixUnitaire();
            totalQuantite += out.getQuantite();
        }
        double prixMoyenSortie = (totalQuantite > 0) ? totalValeur / totalQuantite : 0;

        // Create ListOut
        ListOut listOut = new ListOut();
        listOut.setDateListOut(LocalDateTime.now());
        listOut.setQuantiteTotale(totalQuantite);
        listOut.setPrixMoyenUnitaire(prixMoyenSortie);
        int listOutId = listOutDAO.insert(listOut, nomEntite + "_ListOut");

        // Persist each Out
        for (Out out : sorties) {
            out.setIdListOut(listOutId);
            outDAO.insert(out, nomEntite + "_Out");
        }
    }

    /**
     * Get total available stock.
     */
    public double getStockDisponible(String nomEntite) {
        double totalIn = dynamicDAO.getAggregate(
                "SELECT COALESCE(SUM(quantiteTotale), 0) FROM " + nomEntite + "_ListIn");
        double totalOut = dynamicDAO.getAggregate(
                "SELECT COALESCE(SUM(quantite), 0) FROM " + nomEntite + "_Out");
        return totalIn - totalOut;
    }

    public List<ListIn> getStockListIn(String nomEntite) {
        return listInDAO.findAll(ListIn.class, nomEntite + "_ListIn");
    }

    public List<Out> getAllOut(String nomEntite) {
        return outDAO.findAll(Out.class, nomEntite + "_Out");
    }

    public List<ListOut> getAllListOut(String nomEntite) {
        return listOutDAO.findAll(ListOut.class, nomEntite + "_ListOut");
    }

    public List<Map<String, Object>> getAllIn(String nomEntite) {
        return dynamicDAO.findAll(nomEntite + "_In");
    }

    public void deleteIn(String nomEntite, int id) {
        dynamicDAO.delete(nomEntite + "_In", id);
    }

    public void deleteListIn(String nomEntite, int id) {
        dynamicDAO.deleteWhere(nomEntite + "_In", "idListIn = ?", id);
        dynamicDAO.delete(nomEntite + "_ListIn", id);
    }

    private double getConsumedQuantity(String nomEntite, int listInId) {
        return dynamicDAO.getAggregate(
                "SELECT COALESCE(SUM(quantite), 0) FROM " + nomEntite + "_Out WHERE idListInSource = ?",
                listInId);
    }

    private SortieStrategy getStrategy(StockMethode methode) {
        switch (methode) {
            case FIFO: return new FifoStrategy();
            case LIFO: return new LifoStrategy();
            case CUMP: return new CumpStrategy();
            default: throw new IllegalArgumentException("Methode inconnue: " + methode);
        }
    }
}
