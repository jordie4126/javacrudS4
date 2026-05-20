package ui;

import model.EntiteChamp;
import service.StockService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Panel for managing In entries with dynamic fields.
 */
public class InPanel extends JPanel {

    private String nomEntite;
    private List<EntiteChamp> champs;
    private StockService stockService;

    private DynamicFormPanel formPanel;
    private DynamicTablePanel tablePanel;

    public InPanel(String nomEntite, List<EntiteChamp> champs, StockService stockService) {
        this.nomEntite = nomEntite;
        this.champs = champs;
        this.stockService = stockService;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        // Top: form + buttons
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));

        formPanel = new DynamicFormPanel(champs);
        topPanel.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton btnAjouter = new JButton("Ajouter");
        btnAjouter.addActionListener(e -> ajouter());
        JButton btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(e -> supprimer());
        JButton btnRefresh = new JButton("Rafraichir");
        btnRefresh.addActionListener(e -> refreshTable());
        JButton btnClear = new JButton("Vider");
        btnClear.addActionListener(e -> formPanel.clearForm());

        btnPanel.add(btnAjouter);
        btnPanel.add(btnSupprimer);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnClear);
        topPanel.add(btnPanel, BorderLayout.SOUTH);

        // Bottom: table
        String[] cols = buildColumnNames();
        tablePanel = new DynamicTablePanel(cols);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, tablePanel);
        splitPane.setDividerLocation(220);
        splitPane.setOneTouchExpandable(true);

        add(splitPane, BorderLayout.CENTER);
    }

    private String[] buildColumnNames() {
        List<String> cols = new ArrayList<>();
        cols.add("id");
        cols.add("idlistin");
        cols.add("datein");
        cols.add("quantite");
        cols.add("prixunitaire");
        for (EntiteChamp ch : champs) {
            cols.add(ch.getNomChamp().toLowerCase());
        }
        return cols.toArray(new String[0]);
    }

    private void refreshTable() {
        try {
            List<Map<String, Object>> data = stockService.getAllIn(nomEntite);
            tablePanel.refresh(data);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouter() {
        try {
            Map<String, Object> data = formPanel.readValues();
            double quantite = formPanel.getQuantite();
            double prixUnitaire = formPanel.getPrixUnitaire();

            // Create ListIn
            int listInId = stockService.creerListIn(nomEntite, quantite, prixUnitaire, formPanel.getDateTime());
            data.put("idListIn", listInId);

            // Insert In
            stockService.entree(nomEntite, data);
            formPanel.clearForm();
            refreshTable();

            JOptionPane.showMessageDialog(this, "Entree ajoutee!", "Succes", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimer() {
        int selectedId = tablePanel.getSelectedId();
        if (selectedId < 0) {
            JOptionPane.showMessageDialog(this, "Selectionnez une ligne.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer l'entree id=" + selectedId + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                stockService.deleteIn(nomEntite, selectedId);
                refreshTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
